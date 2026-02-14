package org.payriff.springboot.entities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import org.payriff.springboot.utilities.constants.currency;

@Embeddable
public final class Money {
    
    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column
    @Enumerated(EnumType.STRING)
    private currency currency;

    public Money() {

    }

    public Money(BigDecimal amount, currency currency) {
        Objects.requireNonNull(amount);
        Objects.requireNonNull(currency);

        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public currency getCurrency() {
        return this.currency;
    }
}
