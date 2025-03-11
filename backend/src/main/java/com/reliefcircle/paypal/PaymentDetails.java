package com.reliefcircle.paypal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentDetails {
    private double amount;
    private String email;
    private String currencyCode;
}