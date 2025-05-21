package com.reliefcircle.service;

import com.reliefcircle.dto.DonorStatsDto;
import com.reliefcircle.dto.FundraiserStatsDto;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;



@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final CharityRepository charityRepository;
    private final DonationRepository donationRepository;

    @Transactional(readOnly = true)
    public FundraiserStatsDto getFundraiserStatistics(UUID fundraiserId) {
        log.debug("Calculating statistics for fundraiser ID: {}", fundraiserId);

        // Get total charities
        long totalCharities = charityRepository.countByFundraiserId(fundraiserId);

        // Get total money received
        BigDecimal totalMoneyReceived = charityRepository.sumRaisedAmountByFundraiserId(fundraiserId)
                .orElse(BigDecimal.ZERO);

        // Get unique donors count
        long totalDonors = donationRepository.countUniqueDonorsByFundraiserId(fundraiserId);

        return FundraiserStatsDto.builder()
                .totalCharities(totalCharities)
                .totalMoneyReceived(totalMoneyReceived)
                .totalDonors(totalDonors)
                .build();
    }

    @Transactional(readOnly = true)
    public DonorStatsDto getDonorStatistics(UUID donorId) {
        log.debug("Calculating statistics for donor ID: {}", donorId);

        // Get total amount donated
        BigDecimal totalDonated = donationRepository.sumAmountByDonorId(donorId)
                .orElse(BigDecimal.ZERO);

        // Get count of unique charities donated to
        long totalCharities = donationRepository.countUniqueCharitiesByDonorId(donorId);

        return DonorStatsDto.builder()
                .totalDonated(totalDonated)
                .totalCharitiesSupported(totalCharities)
                .build();
    }
}
