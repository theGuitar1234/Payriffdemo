package org.payriff.springboot.services;

import org.payriff.springboot.records.CreateOrderRequest;
import org.payriff.springboot.records.CreateOrderResponse;
import org.payriff.springboot.records.PayriffOrderInfo;

public interface PayriffService {
    CreateOrderResponse createOrder(CreateOrderRequest request);
    PayriffOrderInfo getOrderInfo(String orderId);
}
