package org.payriff.springboot.records;

public record StartPaymentRecord (
    String amount,
    String currency,
    String description,
    String callbackUrl
) {}
