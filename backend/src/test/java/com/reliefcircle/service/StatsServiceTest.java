package com.reliefcircle.service;

import com.reliefcircle.dto.DonorStatsDto;
import com.reliefcircle.dto.FundraiserStatsDto;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private CharityRepository charityRepository;

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private StatsService statsService;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void getFundraiserStatistics_ShouldReturnCorrectStats() {
        // Arrange
        when(charityRepository.countByFundraiserId(userId)).thenReturn(5L);
        when(charityRepository.sumRaisedAmountByFundraiserId(userId))
                .thenReturn(Optional.of(BigDecimal.valueOf(10000)));
        when(donationRepository.countUniqueDonorsByFundraiserId(userId)).thenReturn(20L);

        // Act
        FundraiserStatsDto stats = statsService.getFundraiserStatistics(userId);

        // Assert
        assertThat(stats.getTotalCharities()).isEqualTo(5L);
        assertThat(stats.getTotalMoneyReceived()).isEqualTo(BigDecimal.valueOf(10000));
        assertThat(stats.getTotalDonors()).isEqualTo(20L);
    }

    @Test
    void getDonorStatistics_ShouldReturnCorrectStats() {
        // Arrange
        when(donationRepository.sumAmountByDonorId(userId))
                .thenReturn(Optional.of(BigDecimal.valueOf(1000)));
        when(donationRepository.countUniqueCharitiesByDonorId(userId)).thenReturn(3L);

        // Act
        DonorStatsDto stats = statsService.getDonorStatistics(userId);

        // Assert
        assertThat(stats.getTotalDonated()).isEqualTo(BigDecimal.valueOf(1000));
        assertThat(stats.getTotalCharitiesSupported()).isEqualTo(3L);
    }
}