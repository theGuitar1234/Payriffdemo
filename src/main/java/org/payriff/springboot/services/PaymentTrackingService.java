package org.payriff.springboot.services;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentTrackingService {
    String createPendingUserDue(Long userId, HttpServletRequest request);
    void settleFromPayriffCallback(String token);
    void reconcilePendingPayments();
}
