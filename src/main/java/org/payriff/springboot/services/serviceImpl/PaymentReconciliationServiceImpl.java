package org.payriff.springboot.services.serviceImpl;

import org.payriff.springboot.services.PaymentReconciliationService;
import org.payriff.springboot.services.PaymentTrackingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PaymentReconciliationServiceImpl implements PaymentReconciliationService {

    private PaymentTrackingService paymentTrackingService;

    public PaymentReconciliationServiceImpl(
        PaymentTrackingService paymentTrackingService
    ) {
        this.paymentTrackingService = paymentTrackingService;
    }

    @Override
    @Scheduled(fixedDelay = 60_000)
    public void reconcile() {
        paymentTrackingService.reconcilePendingPayments();
    }
    
}
