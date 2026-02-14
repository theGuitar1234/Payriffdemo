package org.payriff.springboot.records;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CreateOrderRequest (
    double amount,
    String language,
    String currency,
    String description,
    String callbackUrl,
    boolean cardSave,
    String operation,
    Map<String, String> metadata
) {}
