package com.reliefcircle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDto {
    private Long id;
    private UUID volunteerId;
    private String volunteerName;
    private String volunteerEmail;
    private Long charityId;
    private String charityName;
    private Long proofId;
    private String proofDescription;
    private String status;
    private String comment;
    private LocalDateTime reviewedAt;
}