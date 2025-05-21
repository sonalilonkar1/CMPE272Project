package com.reliefcircle.controller;

import com.reliefcircle.dto.DonorStatsDto;
import com.reliefcircle.dto.FundraiserStatsDto;
import com.reliefcircle.model.User;
import com.reliefcircle.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    /**
     * Fetches statistics for the logged-in fundraiser.
     *
     * @param authentication The authentication object containing user details.
     * @return ResponseEntity containing the statistics.
     */
    @GetMapping("/fundraiser")
    public ResponseEntity<FundraiserStatsDto> getFundraiserStatistics(Authentication authentication) {
        log.info("Fetching statistics for logged-in fundraiser");
        
        User fundraiser = (User) authentication.getPrincipal();
        FundraiserStatsDto stats = statsService.getFundraiserStatistics(fundraiser.getId());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Fetches statistics for the logged-in donor.
     *
     * @param authentication The authentication object containing user details.
     * @return ResponseEntity containing the statistics.
     */
    @GetMapping("/donor")
    public ResponseEntity<DonorStatsDto> getDonorStatistics(Authentication authentication) {
        log.info("Fetching statistics for logged-in donor");
        
        User donor = (User) authentication.getPrincipal();
        DonorStatsDto stats = statsService.getDonorStatistics(donor.getId());
        
        return ResponseEntity.ok(stats);
    }
}