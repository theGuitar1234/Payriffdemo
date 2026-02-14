package org.payriff.springboot.services.serviceImpl;

import org.payriff.springboot.clients.PayriffClient;
import org.payriff.springboot.records.CreateOrderRequest;
import org.payriff.springboot.records.CreateOrderResponse;
import org.payriff.springboot.records.PayriffOrderInfo;
import org.payriff.springboot.services.PayriffService;
import org.springframework.stereotype.Service;

@Service
public class PayriffServiceImpl implements PayriffService {
    
    private final PayriffClient payriffClient;

    public PayriffServiceImpl(
        PayriffClient payriffClient
    ) {
        this.payriffClient = payriffClient;
    }

    @Override
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        return payriffClient.createOrder(request);
    }

    @Override
    public PayriffOrderInfo getOrderInfo(String orderId) {
        return payriffClient.getOrderInfo(orderId);
    }
    
}
