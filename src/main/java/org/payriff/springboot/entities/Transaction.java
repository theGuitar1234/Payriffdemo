package org.payriff.springboot.entities;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.payriff.springboot.utilities.constants.currency;
import org.payriff.springboot.utilities.constants.transactionstatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="TRANSACTIONS")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payment_provider", nullable = false)
    private String paymentProvider = "PAYRIFF";

    @Column(name = "provider_order_id", nullable = true)
    private String providerOrderId;

    @Column(name = "provider_transaction_id", nullable = true)
    private Long providerTransactionId;

    @Column(name = "provider_response_id", nullable = true)
    private String providerResponseId;

    @Column(name = "provider_payment_url", nullable = true)
    private String providerPaymentUrl;

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal transactionAmount;

    @Column(name = "transaction_time", nullable = false)
    private Instant transactionTime;

    @Column(name = "transaction_fee", nullable = false)
    private BigDecimal transactionFee;

    @Column(name = "transaction_total", nullable = false)
    private BigDecimal transactionTotal;

    @Column(name = "transaction_description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "transaction_token", nullable = false, updatable = false)
    private String token;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "next_retry_at", nullable = false)
    private Instant nextRetryAt;

    @Column(name = "last_error_at", nullable = true)
    private Instant lastErrorAt;

    @Column(name = "last_error", nullable = true)
    private String lastError;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private transactionstatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private currency currency;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
