package com.reliefcircle.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DonorStatsDto {
    private BigDecimal totalDonated;
    private long totalCharitiesSupported;
}