package com.reliefcircle.controller;

import com.reliefcircle.dto.DonorStatsDto;
import com.reliefcircle.dto.FundraiserStatsDto;
import com.reliefcircle.model.User;
import com.reliefcircle.service.StatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    private User mockUser;
    private FundraiserStatsDto fundraiserStats;
    private DonorStatsDto donorStats;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        mockUser = User.builder()
                .id(userId)
                .email("test@example.com")
                .build();

        fundraiserStats = FundraiserStatsDto.builder()
                .totalCharities(5L)
                .totalMoneyReceived(BigDecimal.valueOf(10000))
                .totalDonors(20L)
                .build();

        donorStats = DonorStatsDto.builder()
                .totalDonated(BigDecimal.valueOf(1000))
                .totalCharitiesSupported(3L)
                .build();
    }

    @Test
    @WithMockUser(roles = "FUNDRAISER")
    void getFundraiserStatistics_ShouldReturnStats() throws Exception {
        when(statsService.getFundraiserStatistics(any(UUID.class)))
                .thenReturn(fundraiserStats);

        mockMvc.perform(get("/api/stats/fundraiser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCharities").value(5))
                .andExpect(jsonPath("$.totalMoneyReceived").value(10000))
                .andExpect(jsonPath("$.totalDonors").value(20));
    }

    @Test
    @WithMockUser(roles = "DONOR")
    void getDonorStatistics_ShouldReturnStats() throws Exception {
        when(statsService.getDonorStatistics(any(UUID.class)))
                .thenReturn(donorStats);

        mockMvc.perform(get("/api/stats/donor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDonated").value(1000))
                .andExpect(jsonPath("$.totalCharitiesSupported").value(3));
    }

    @Test
    @WithMockUser(roles = "DONOR")
    void getFundraiserStatistics_WithDonorRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/stats/fundraiser"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "FUNDRAISER")
    void getDonorStatistics_WithFundraiserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/stats/donor"))
                .andExpect(status().isForbidden());
    }
}