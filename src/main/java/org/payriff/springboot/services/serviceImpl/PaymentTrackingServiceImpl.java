package org.payriff.springboot.services.serviceImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.payriff.springboot.entities.User;
import org.payriff.springboot.entities.Transaction;
import org.payriff.springboot.records.CreateOrderRequest;
import org.payriff.springboot.records.CreateOrderResponse;
import org.payriff.springboot.records.PayriffOrderInfo;
import org.payriff.springboot.repositories.UserRepository;
import org.payriff.springboot.repositories.TransactionRepository;
import org.payriff.springboot.services.PaymentTrackingService;
import org.payriff.springboot.services.PayriffService;
import org.payriff.springboot.utilities.constants.currency;
import org.payriff.springboot.utilities.constants.transactionstatus;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class PaymentTrackingServiceImpl implements PaymentTrackingService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    private final PayriffService payriffService;

    public PaymentTrackingServiceImpl(
        UserRepository userRepository,
        TransactionRepository transactionRepository,
        PayriffService payriffService
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.payriffService = payriffService;
    }

    @Override
    @Transactional
    public String createPendingUserDue(
        Long userId, 
        HttpServletRequest request
    ) {

        if (userId == null) throw new NullPointerException("User ID is null");

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User Not Found"));

        BigDecimal amount = user.getPayment().getAmount();
        currency currency = user.getPayment().getCurrency();

        Transaction transaction = new Transaction();
        transaction.setStatus(transactionstatus.PENDING);
        transaction.setCurrency(currency);
        transaction.setTransactionAmount(amount);
        transaction.setTransactionFee(BigDecimal.ZERO);
        transaction.setTransactionTotal(amount.add(BigDecimal.ZERO));
        transaction.setDescription("User dues payment via PayRiff");
        transaction.setPaymentProvider("PAYRIFF");

        String token = UUID.randomUUID().toString();
        
        transaction.setToken(token);
        transaction.setAttemptCount(0);
        transaction.setNextRetryAt(Instant.now());
        transaction.setUser(user);
        
        transactionRepository.saveAndFlush(transaction);

        String baseUrl = request.getScheme() + "://" + request.getServerName()
            + ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort());

        String callBackUrl = baseUrl + "/admin/payriff/callback?pid=" + token;

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
            transaction.getTransactionAmount().doubleValue(),
            "EN",
            transaction.getCurrency().name(),
            transaction.getDescription(),
            callBackUrl,
            false,
            "PURCHASE",
            Map.of(
                "token", token, 
                "transactionId", transaction.getId().toString(), 
                "userId", userId.toString())
        );

        try {
            CreateOrderResponse response = payriffService.createOrder(createOrderRequest);
            
            transaction.setProviderOrderId(response.payload().orderId());
            transaction.setProviderResponseId(response.responseId());
            transaction.setProviderTransactionId(response.payload().transactionId());
            transaction.setProviderPaymentUrl(response.payload().paymentUrl());

            transaction.setAttemptCount(transaction.getAttemptCount() + 1);
            transaction.setNextRetryAt(Instant.now().plusSeconds(30));

            Transaction savedTransaction = transactionRepository.saveAndFlush(transaction);

            return savedTransaction.getProviderPaymentUrl(); 
        } catch (Exception e) {
            transaction.setStatus(transactionstatus.ERROR);
            transaction.setLastErrorAt(Instant.now());
            transaction.setLastError(e.getMessage());
            transaction.setAttemptCount(transaction.getAttemptCount() + 1);
            transaction.setNextRetryAt(Instant.now().plusSeconds(30));
            
            transactionRepository.saveAndFlush(transaction);
            throw new RuntimeException("Failed to Create Payriff Order");
        }
    }

    @Override
    @Transactional
    public void settleFromPayriffCallback(String token) {
        Transaction transaction = transactionRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Transaction Not Found by Token"));
        
        if (
            transaction.getStatus() == transactionstatus.PAID ||
            transaction.getStatus() == transactionstatus.DECLINED ||
            transaction.getStatus() == transactionstatus.CANCELLED ||
            transaction.getStatus() == transactionstatus.EXPIRED
        ) {
            return;
        }

        if (transaction.getProviderOrderId() == null) {
            transaction.setStatus(transactionstatus.ERROR);
            transaction.setLastError("No provider orderId stored");
            transactionRepository.saveAndFlush(transaction);
        }

        PayriffOrderInfo info = payriffService.getOrderInfo(transaction.getProviderOrderId());

        transactionstatus newStatus = mapPayriffStatus(info.payload().paymentStatus());
        transaction.setStatus(newStatus);
        transaction.setTransactionTime(Instant.now());
        transactionRepository.saveAndFlush(transaction);

        if (newStatus == transactionstatus.PAID) {
            User user = transaction.getUser();
            user.updateNextPaymentDate();
            userRepository.saveAndFlush(user);
        }
    }

    private transactionstatus mapPayriffStatus(String payriffStatus) {
        if (payriffStatus == null) return transactionstatus.ERROR;
        return switch (payriffStatus.toUpperCase()) {
            case "PAID", "APPROVED" -> transactionstatus.PAID;
            case "DECLINED" -> transactionstatus.DECLINED;
            case "CANCELED" -> transactionstatus.CANCELLED;
            case "EXPIRED" -> transactionstatus.EXPIRED;
            case "CREATED" -> transactionstatus.PENDING;
            default -> transactionstatus.ERROR;
        };
    }

    @Override
    @Transactional
    public void reconcilePendingPayments() {
        List<Transaction> transactions = transactionRepository
            .findTop50ByPaymentProviderAndStatusInAndNextRetryAtBeforeOrderByNextRetryAtAsc(
                "PAYRIFF", 
                List.of(transactionstatus.PENDING, transactionstatus.ERROR),
                Instant.now()
            );
        for (Transaction transaction : transactions) {
            try {
                if (transaction.getProviderOrderId() == null) {
                    transaction.setStatus(transactionstatus.ERROR);
                    transaction.setLastError("Missing providerOrderId, can't reconcile");
                    transaction.setLastErrorAt(Instant.now());
                    transaction.setNextRetryAt(Instant.now().plusSeconds(300));
                    transactionRepository.save(transaction);
                    continue;
                }
                
                settleFromPayriffCallback(transaction.getToken());

                if (transaction.getStatus() == transactionstatus.PENDING) {
                    int attempt = transaction.getAttemptCount() + 1;
                    transaction.setAttemptCount(attempt);
                    transaction.setNextRetryAt(Instant.now().plusSeconds(Math.min(900, 30L * attempt)));
                    transactionRepository.save(transaction);
                }
            } catch (Exception e) {
                int attempt = transaction.getAttemptCount() + 1;
                transaction.setAttemptCount(attempt);
                transaction.setStatus(transactionstatus.ERROR);
                transaction.setLastError(e.getMessage());
                transaction.setLastErrorAt(Instant.now());
                transaction.setNextRetryAt(Instant.now().plusSeconds(Math.min(1800, 60L * attempt)));
                transactionRepository.save(transaction);
            }
        }

        transactionRepository.flush();
    }
    
}
