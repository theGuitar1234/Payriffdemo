package org.payriff.springboot.records;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PayriffOrderInfo (
    String code, 
    String message,
    String route, 
    String internalMessage,
    String responseId,
    Payload payload
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Payload (
        String orderId, 
        String amount,
        String currencyType, 
        String merchantName, 
        String operationType, 
        String paymentStatus, 
        String auto, 
        String createdDate, 
        String description,
        List<TransactionRecord> transactions
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record TransactionRecord (
            String uuid,
            String createdDate,
            String status,
            String channel,
            String channelType,
            String requestRrn,
            String responseRrn,
            String pan,
            String paymentWay,
            CardDetails cardDetails,
            String merchantCategory,
            Installment installment
        ) {}
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record CardDetails(
            String maskedPan,
            String brand,
            String cardHolderName
        ) {}
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Installment (
            String type,
            String period
        ) {}
    }
}
