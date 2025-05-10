package com.reliefcircle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStripeIdRequest {

    @NotBlank(message = "Stripe ID cannot be blank")
    private String stripeId;
}