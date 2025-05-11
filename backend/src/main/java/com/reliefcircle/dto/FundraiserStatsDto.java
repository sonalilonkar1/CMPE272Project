package com.reliefcircle.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class FundraiserStatsDto {
    private long totalCharities;
    private BigDecimal totalMoneyReceived;
    private long totalDonors;
}