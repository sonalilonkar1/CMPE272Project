package com.reliefcircle.service;

import com.reliefcircle.dto.UpdateDto;
import com.reliefcircle.dto.UpdateRatingDto;
import com.reliefcircle.model.Charity;
import com.reliefcircle.model.Update;
import com.reliefcircle.dto.UpdateRatingDto;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.UpdateRepository;
import com.reliefcircle.repository.UpdateRatingRepository;
import com.reliefcircle.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateServiceTest {

    @Mock
    private UpdateRepository updateRepository;

    @Mock
    private UpdateRatingRepository updateRatingRepository;

    @Mock
    private CharityRepository charityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AWSService awsService;

    @InjectMocks
    private UpdateService updateService;

    private User mockFundraiser;
    private Charity mockCharity;
    private Update mockUpdate;
    private UpdateDto mockUpdateDto;

    @BeforeEach
    void setUp() {
        mockFundraiser = User.builder()
                .id(UUID.randomUUID())
                .email("fundraiser@example.com")
                .role(User.UserRole.FUNDRAISER)
                .build();

        mockCharity = Charity.builder()
                .id(1L)
                .fundraiser(mockFundraiser)
                .name("Test Charity")
                .isVerified(false) // Initial state
                .build();

        mockUpdate = Update.builder()
                .id(1L)
                .charity(mockCharity)
                .fundraiser(mockFundraiser)
                .text("Test update")
                .build();

        mockUpdateDto = UpdateDto.builder()
                .id(1L)
                .charityId(1L)
                .fundraiserId(mockFundraiser.getId())
                .text("Test update")
                .build();

        // Mock repository calls
        when(userRepository.findById(eq(mockFundraiser.getId()))).thenReturn(Optional.of(mockFundraiser));
        when(charityRepository.findById(eq(mockCharity.getId()))).thenReturn(Optional.of(mockCharity));
        when(updateRepository.findById(eq(mockUpdate.getId()))).thenReturn(Optional.of(mockUpdate));
    }

    @Test
    void createUpdate_ShouldSaveAndReturnUpdate() {
        // Arrange
        when(updateRepository.save(any(Update.class))).thenReturn(mockUpdate);

        // Act
        UpdateDto result = updateService.createUpdate(mockUpdateDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo(mockUpdateDto.getText());
        verify(updateRepository).save(any(Update.class));
    }

    @Test
    void getUpdatesByCharity_ShouldReturnPageOfUpdates() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Update> updatePage = new PageImpl<>(List.of(mockUpdate));
        when(updateRepository.findByCharityId(eq(mockCharity.getId()), eq(pageRequest)))
                .thenReturn(updatePage);

        // Act
        Page<UpdateDto> result = updateService.getUpdatesByCharity(
                mockCharity.getId(),
                pageRequest
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getText()).isEqualTo(mockUpdate.getText());
    }

    @Test
        void checkAndUpdateApprovalStatus_WhenInsufficientRatings_ShouldNotVerifyCharity() {
        // Arrange
        when(updateRatingRepository.countByUpdateId(eq(mockUpdate.getId()))).thenReturn(2L);
        when(updateRatingRepository.getAverageRatingForUpdate(eq(mockUpdate.getId()))).thenReturn(8.0);

        // Act
        updateService.checkAndUpdateApprovalStatus(mockUpdate.getId());

        // Assert
        verify(charityRepository, never()).save(any(Charity.class));
        assertThat(mockCharity.getIsVerified()).isFalse();
    }

    @Test
        void checkAndUpdateApprovalStatus_WhenLowAverageRating_ShouldNotVerifyCharity() {
        // Arrange
        when(updateRatingRepository.countByUpdateId(eq(mockUpdate.getId()))).thenReturn(3L);
        when(updateRatingRepository.getAverageRatingForUpdate(eq(mockUpdate.getId()))).thenReturn(6.0);

        // Act
        updateService.checkAndUpdateApprovalStatus(mockUpdate.getId());

        // Assert
        verify(charityRepository, never()).save(any(Charity.class));
        assertThat(mockCharity.getIsVerified()).isFalse();
    }
    

}