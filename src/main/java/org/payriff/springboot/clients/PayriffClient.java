package org.payriff.springboot.clients;

import org.payriff.springboot.records.CreateOrderRequest;
import org.payriff.springboot.records.CreateOrderResponse;
import org.payriff.springboot.records.PayriffOrderInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class PayriffClient {

    private final WebClient webClient;

    private static final String PUBLIC_DOMAIN = "https://api.payriff.com/api/v3";

    public PayriffClient(
        WebClient.Builder builder,
        @Value("${payriff.base-url:https://api.payriff.com/api/v3}") String baseUrl,
        @Value("${payriff.secret-key}") String secretKey
    ) {
        String url = baseUrl != null ? baseUrl : PUBLIC_DOMAIN; 
        this.webClient = builder
            .baseUrl(url)
            .defaultHeader(HttpHeaders.AUTHORIZATION, secretKey)
            .build();
    }

    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        if (request == null) throw new NullPointerException("No Request Provided");
        return webClient.post()
            .uri("/orders")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(CreateOrderResponse.class)
            .block();
    }

    public PayriffOrderInfo getOrderInfo(String orderId) {
        return webClient.get()
            .uri("/orders/{id}", orderId)
            .retrieve()
            .bodyToMono(PayriffOrderInfo.class)
            .block();
    }
    
}
