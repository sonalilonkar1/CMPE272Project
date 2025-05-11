package com.reliefcircle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharityDto {
    private Long id;
    private String name;
    private String organizationName;
    private String description;
    private String category;
    private BigDecimal targetAmount;
    private BigDecimal raisedAmount;
    private Boolean isVerified;
    private LocalDateTime createdAt;
    private UUID fundraiserId;
    private String fundraiserName;
    private String fundraiserEmail;
    private String fundraiserStripeId;
    private MultipartFile file;
    private String fileUrl;
}
