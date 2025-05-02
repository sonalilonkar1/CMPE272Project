package com.reliefcircle.dto;

import com.reliefcircle.model.Donation;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationDto {
    private Long id;
    private String paypalOrderId;
    private UUID donorId;
    private String donorName;
    private String donorEmail;
    private Long charityId;
    private String charityName;
    private Double amount;
    private String paymentStatus;
    private String transactionId;
    private Donation.DonationStatus status;
    private LocalDateTime createdAt;

    public static DonationDto fromEntity(Donation donation) {
        DonationDto dto = new DonationDto();
        dto.setId(donation.getId());
        dto.setPaypalOrderId(donation.getPaypalOrderId());
        dto.setDonorId(donation.getDonor().getId());
        dto.setCharityId(donation.getCharity().getId());
        dto.setAmount(donation.getAmount());
        dto.setPaymentStatus(donation.getPaymentStatus());
        dto.setTransactionId(donation.getTransactionId());
        dto.setStatus(donation.getStatus());
        dto.setCreatedAt(donation.getCreatedAt());
        return dto;
    }
}