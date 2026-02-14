package org.payriff.springboot.controllers;

import org.payriff.springboot.services.PaymentTrackingService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/api/payments")
public class PayriffController {

    private final PaymentTrackingService paymentTrackingService;

    public PayriffController(
        PaymentTrackingService paymentTrackingService
    ) {
        this.paymentTrackingService = paymentTrackingService;
    }

    @PostMapping("/payriff/start")
    public String startPayriffCheckout(
        @RequestParam(name = "id") Long id,
        HttpServletRequest request
    ) {
        String providerPaymentUrl = paymentTrackingService.createPendingUserDue(id, request);
        return "redirect:" + providerPaymentUrl;
    }

    @GetMapping("/payriff/callback")
    public String payriffCallback(
        @RequestParam("pid") String token
    ) {
        paymentTrackingService.settleFromPayriffCallback(token);
        return "redirect:/?payment=callback";
    }
    
}