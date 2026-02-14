package org.payriff.springboot.controllers;

import java.util.Map;
import java.util.UUID;

import org.payriff.springboot.records.CreateOrderRequest;
import org.payriff.springboot.records.CreateOrderResponse;
import org.payriff.springboot.records.PayriffOrderInfo;
import org.payriff.springboot.services.PayriffService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/api/payments")
public class PayriffController {

    private final PayriffService payriffService;

    @Value("${public.domain:https://sackclothed-marlana-nongeological.ngrok-free.dev}")
    private String publicDomain;

    public PayriffController(
        PayriffService payriffService
    ) {
        this.payriffService = payriffService;
    }

    @PostMapping("/payriff/start")
    public String startPayriffCheckout(
        @RequestParam(name = "amount", required = true) String amount,
        @RequestParam(name = "currency") String currency,
        @RequestParam(name = "description") String description,
        HttpServletRequest request
    ) {
        //  internalOrderId = UUID.randomUUID().toString();

        //String callBackUrl = publicDomain + "/api/payments/payriff/callback";

        // String baseUrl = request.getScheme() + "://" + request.getServerName()
        //     + ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort());

        // String callBackUrl = baseUrl + "/admin/payriff/callback?pid=" + internalOrderId;

        CreateOrderRequest createOrderRequest = new CreateOrderRequest(
            Double.valueOf(amount),
            "EN",
            currency,
            description,
            callBackUrl,
            false,
            "PURCHASE",
            Map.of("internalOrderId", internalOrderId)
        );

        CreateOrderResponse response;

        try {
            response = payriffService.createOrder(createOrderRequest);
        } catch (NullPointerException e) {
            return "Something went wrong";//ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getLocalizedMessage());
        }

        if (response == null || 
            response.payload() == null ||
            response.payload().paymentUrl() == null
        ) {
            return "Something went wrong because of null"; //ResponseEntity.badRequest().body(Map.of("error", "Failed to create PayRiff order"));
        }

        // return ResponseEntity.ok(Map.of(
        //     "internalOrderId", internalOrderId,
        //     "payriffOrderId", response.payload().orderId(),
        //     "redirectUrl", response.payload().paymentUrl()
        // ));
        // return ResponseEntity.ok(response);

        System.out.println("\n\n\n\n\n\n\n\n" + response.payload().paymentUrl() + "\n\n\n\n\n\n\n");
        
        return "redirect:" + response.payload().paymentUrl();
    }

    @GetMapping("/payriff/callback")
    public ResponseEntity<PayriffOrderInfo> payriffCallback(
        @RequestParam("pid") String pid,
        Model model
    ) {
        PayriffOrderInfo info = payriffService.getOrderInfo(pid);

        if ("PAID".equalsIgnoreCase((info.payload().paymentStatus()))) {
            return ResponseEntity.ok(info);
        }

        return ResponseEntity.badRequest().body(info);
    }
    
}