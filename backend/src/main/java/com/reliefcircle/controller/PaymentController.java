package com.reliefcircle.controller;

import com.reliefcircle.service.StripeService;
import com.stripe.model.PaymentIntent;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final StripeService stripeService;

    public PaymentController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/create-payment-intent")
    public String createPaymentIntent(@RequestBody PaymentRequest request) throws Exception {
        PaymentIntent intent = stripeService.createPaymentIntent(request.getAmount(), request.getCurrency());
        return intent.getClientSecret(); // Send this to the frontend
    }

    // @PostMapping("/webhook")
    // public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {
    //     // Parse and verify event, then handle accordingly
    // }

    public static class PaymentRequest {
        private Long amount;
        private String currency;
        // getters and setters
        public Long getAmount() { return amount; }
        public void setAmount(Long amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
    }
}