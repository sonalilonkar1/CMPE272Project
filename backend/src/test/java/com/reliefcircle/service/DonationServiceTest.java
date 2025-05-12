package com.reliefcircle.service;

import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.model.Charity;
import com.reliefcircle.model.Donation;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.DonationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    @Mock
    private DonationRepository donationRepository;

    @InjectMocks
    private DonationService donationService;

    private User donor;
    private Charity charity;
    private Donation donation;

    @BeforeEach
    void setUp() {
        donor = User.builder()
                .id(UUID.randomUUID())
                .email("donor@test.com")
                .build();

        charity = Charity.builder()
                .id(1L)
                .name("Test Charity")
                .build();

        donation = Donation.builder()
                .id(1L)
                .donor(donor)
                .charity(charity)
                .amount(100.00)
                .build();

    }

    @Test
    void getDonationsByDonor_ShouldReturnPageOfDonations() {
        // Arrange
        Page<Donation> donationPage = new PageImpl<>(List.of(donation));
        when(donationRepository.findByDonorId(any(), any(PageRequest.class)))
                .thenReturn(donationPage);

        // Act
        Page<DonationDto> result = donationService.getDonationsByDonor(
                donor.getId(),
                PageRequest.of(0, 10)
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAmount())
                .isEqualByComparingTo(donation.getAmount());
    }
}