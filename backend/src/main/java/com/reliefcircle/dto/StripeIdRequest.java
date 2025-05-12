package com.reliefcircle.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StripeIdRequest {
    private String stripeId;
    public String getStripeId() { return stripeId; }
    public void setStripeId(String stripeId) { this.stripeId = stripeId; }
}