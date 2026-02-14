package org.payriff.springboot.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateOrderResponse (
    String code,
    String message,
    String responseId,
    Payload payload
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Payload (
        String orderId,
        String paymentUrl,
        Long transactionId
    ) {}
}
