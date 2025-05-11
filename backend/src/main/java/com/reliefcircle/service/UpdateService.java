package com.reliefcircle.service;

import com.reliefcircle.dto.UpdateDto;
import com.reliefcircle.dto.UpdateRatingDto;
import com.reliefcircle.dto.PaginatedResponse;
import com.reliefcircle.exception.ResourceNotFoundException;
import com.reliefcircle.model.Charity;
import com.reliefcircle.model.Update;
import com.reliefcircle.model.UpdateRating;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.UpdateRepository;
import com.reliefcircle.repository.UpdateRatingRepository;
import com.reliefcircle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateService {

    private final UpdateRepository updateRepository;
    private final UpdateRatingRepository updateRatingRepository;
    private final UserRepository userRepository;
    private final CharityRepository charityRepository;
    private final AWSService awsService;
    private final NotificationService notificationService;


    /**
     * Create a new update for a charity
     */
    @Transactional
    public UpdateDto createUpdate(UpdateDto updateDto) {
        // Validate charity and fundraiser
        User fundraiser = userRepository.findById(updateDto.getFundraiserId())
                .orElseThrow(() -> new ResourceNotFoundException("Fundraiser not found with ID: " + updateDto.getFundraiserId()));
        
        if (fundraiser.getRole() != User.UserRole.FUNDRAISER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only fundraisers can create updates");
        }
        
        Charity charity = charityRepository.findById(updateDto.getCharityId())
                .orElseThrow(() -> new ResourceNotFoundException("Charity not found with ID: " + updateDto.getCharityId()));
        
        // Ensure the fundraiser is the owner of the charity
        if (!charity.getFundraiser().getId().equals(fundraiser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Fundraiser is not the owner of this charity");
        }
        
        // Handle file upload if provided
        String fileUrl = null;
        if (updateDto.getFile() != null && !updateDto.getFile().isEmpty()) {
            MultipartFile file = updateDto.getFile();
            String folder = "charity-updates/" + charity.getId();
            fileUrl = awsService.uploadProofDocument(file, folder);
        }
        
        // Create update entity
        Update update = Update.builder()
                .fundraiser(fundraiser)
                .charity(charity)
                .text(updateDto.getText())
                .fileUrl(fileUrl)
                .build();
        
        // Save to database
        Update savedUpdate = updateRepository.save(update);
        
        // Convert to DTO and return
        return convertToDto(savedUpdate);
    }
    
    /**
     * Get update by ID
     */
    @Transactional(readOnly = true)
    public UpdateDto getUpdateById(Long id) {
        Update update = updateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Update not found with ID: " + id));
        
        return convertToDto(update);
    }
    
    /**
     * Get updates for a charity with pagination
     */
    @Transactional(readOnly = true)
    public Page<UpdateDto> getUpdatesForCharity(Long charityId, Pageable pageable) {
        // Verify charity exists
        if (!charityRepository.existsById(charityId)) {
            throw new ResourceNotFoundException("Charity not found with ID: " + charityId);
        }
        
        Page<Update> updates = updateRepository.findByCharityIdOrderByCreatedAtDesc(charityId, pageable);
        return updates.map(this::convertToDto);
    }
    
    /**
     * Get updates by fundraiser with pagination
     */
    @Transactional(readOnly = true)
    public Page<UpdateDto> getUpdatesByFundraiser(UUID fundraiserId, Pageable pageable) {
        // Verify fundraiser exists
        if (!userRepository.existsById(fundraiserId)) {
            throw new ResourceNotFoundException("Fundraiser not found with ID: " + fundraiserId);
        }
        
        Page<Update> updates = updateRepository.findByFundraiserIdOrderByCreatedAtDesc(fundraiserId, pageable);
        return updates.map(this::convertToDto);
    }
    
    /**
     * Get updates for charities that a donor has donated to
     */
    @Transactional(readOnly = true)
    public Page<UpdateDto> getUpdatesForDonor(UUID donorId, Pageable pageable) {
        // Verify donor exists
        if (!userRepository.existsById(donorId)) {
            throw new ResourceNotFoundException("Donor not found with ID: " + donorId);
        }
        
        Page<Update> updates = updateRepository.findByDonorIdOrderByCreatedAtDesc(donorId, pageable);
        return updates.map(this::convertToDto);
    }
    
    /**
     * Get updates for charities that have been verified by a volunteer
     */
    @Transactional(readOnly = true)
    public Page<UpdateDto> getUpdatesByVolunteer(UUID volunteerId, Pageable pageable) {
        // Verify volunteer exists
        User volunteer = userRepository.findById(volunteerId)
                .orElseThrow(() -> new ResourceNotFoundException("Volunteer not found with ID: " + volunteerId));
        
        if (!volunteer.getIsVolunteer()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a volunteer");
        }
        
        Page<Update> updates = updateRepository.findByVolunteerIdOrderByCreatedAtDesc(volunteerId, pageable);
        return updates.map(this::convertToDto);
    }
    
    /**
     * Update an existing update
     */
    @Transactional
    public UpdateDto updateUpdate1(Long id, UpdateDto updateDto, UUID userId) {
        Update update = updateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Update not found with ID: " + id));
        
        // Check if the current user is the fundraiser who created this update
        if (!update.getFundraiser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the original fundraiser can update this update");
        }
        
        // Update text content
        if (updateDto.getText() != null) {
            update.setText(updateDto.getText());
        }
        
        // Handle file upload if a new file is provided
        if (updateDto.getFile() != null && !updateDto.getFile().isEmpty()) {
            String folder = "charity-updates/" + update.getCharity().getId();
            String fileUrl = awsService.uploadProofDocument(updateDto.getFile(), folder);
            update.setFileUrl(fileUrl);
        }
        
        Update updatedUpdate = updateRepository.save(update);
        return convertToDto(updatedUpdate);
    }
    
    /**
     * Delete an update
     */
    @Transactional
    public void deleteUpdate(Long id, UUID userId) {
        Update update = updateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Update not found with ID: " + id));
        
        // Check if the current user is the fundraiser who created this update
        if (!update.getFundraiser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the original fundraiser can delete this update");
        }
        
        updateRepository.delete(update);
    }
    
    /**
     * Rate an update
     */
    @Transactional
    public UpdateRatingDto rateUpdate(Long updateId, UpdateRatingDto ratingDto, UUID donorId) {
        // Check if the update exists
        Update update = updateRepository.findById(updateId)
                .orElseThrow(() -> new ResourceNotFoundException("Update not found with ID: " + updateId));
        
        // Check if user exists and is a donor
        User donor = userRepository.findById(donorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + donorId));
                
        if (donor.getRole() != User.UserRole.DONOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only donors can rate updates");
        }
        
        // Check if the user has already rated this update
        Optional<UpdateRating> existingRating = updateRatingRepository.findByUpdateIdAndDonorId(updateId, donorId);
        
        if (existingRating.isPresent()) {
            // Update existing rating
            UpdateRating rating = existingRating.get();
            rating.setRating(ratingDto.getRating());
            rating.setComment(ratingDto.getComment());
            
            UpdateRating savedRating = updateRatingRepository.save(rating);
            
            // Recalculate average rating for the update
            update.recalculateAverageRating();
            updateRepository.save(update);
            
            // Check if update should be approved
            checkAndUpdateApprovalStatus(updateId);
            
            return convertToDto(savedRating);
        } else {
            // Create new rating
            UpdateRating rating = UpdateRating.builder()
                    .update(update)
                    .donor(donor)
                    .rating(ratingDto.getRating())
                    .comment(ratingDto.getComment())
                    .build();
            
            UpdateRating savedRating = updateRatingRepository.save(rating);
            
            // Add the rating to the update and recalculate average
            update.addRating(savedRating);
            updateRepository.save(update);
            
            // Check if update should be approved
            checkAndUpdateApprovalStatus(updateId);
            
            return convertToDto(savedRating);
        }
    }
    
    /**
     * Get ratings for an update with pagination
     */
    @Transactional(readOnly = true)
    public Page<UpdateRatingDto> getRatingsForUpdate(Long updateId, Pageable pageable) {
        // Check if the update exists
        if (!updateRepository.existsById(updateId)) {
            throw new ResourceNotFoundException("Update not found with ID: " + updateId);
        }
        
        Page<UpdateRating> ratings = updateRatingRepository.findByUpdateId(updateId, pageable);
        return ratings.map(this::convertToDto);
    }
    
    /**
     * Get a user's rating for an update
     */
    @Transactional(readOnly = true)
    public UpdateRatingDto getUserRatingForUpdate(Long updateId, UUID userId) {
        Optional<UpdateRating> ratingOpt = updateRatingRepository.findByUpdateIdAndDonorId(updateId, userId);
        return ratingOpt.map(this::convertToDto).orElse(null);
    }
    
    /**
     * Delete a rating
     */
    @Transactional
    public void deleteRating(Long ratingId, UUID userId) {
        UpdateRating rating = updateRatingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with ID: " + ratingId));
        
        // Check if the current user is the donor who created this rating
        if (!rating.getDonor().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the original donor can delete this rating");
        }
        
        // Get the update to recalculate average after deleting
        Update update = rating.getUpdate();
        
        // Remove the rating
        update.removeRating(rating);
        updateRatingRepository.delete(rating);
        
        // Save the update with recalculated average
        updateRepository.save(update);
    }
    
    // Convert entities to DTOs
    private UpdateDto convertToDto(Update update) {
        List<UpdateRatingDto> ratingDtos = update.getRatings().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
                
        return UpdateDto.builder()
                .id(update.getId())
                .fundraiserId(update.getFundraiser().getId())
                .fundraiserName(update.getFundraiser().getFullName())
                .fundraiserEmail(update.getFundraiser().getEmail())
                .charityId(update.getCharity().getId())
                .charityName(update.getCharity().getName())
                .text(update.getText())
                .fileUrl(update.getFileUrl())
                .createdAt(update.getCreatedAt())
                .updatedAt(update.getUpdatedAt())
                .averageRating(update.getAverageRating())
                .ratingCount(update.getRatingCount())
                .ratings(ratingDtos)
                .build();
    }
    
    private UpdateRatingDto convertToDto(UpdateRating rating) {
        return UpdateRatingDto.builder()
                .id(rating.getId())
                .updateId(rating.getUpdate().getId())
                .donorId(rating.getDonor().getId())
                .donorName(rating.getDonor().getFullName())
                .donorEmail(rating.getDonor().getEmail())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .updatedAt(rating.getUpdatedAt())
                .build();
    }
    
    /**
     * Send notification to volunteer about a new update
     * @param volunteer
     * @param update
     */
    public void sendNotificationToVolunteer(User volunteer, UpdateDto update) {
        String message = String.format(
            "Hello %s, a new update has been posted for your action. '%s': %s",
            volunteer.getFullName(),
            update.getCharityId(),
            update.getText()
        );

        // Use the NotificationService to send the notification
        notificationService.sendNotification(volunteer.getEmail(), "New Charity Update", message);
    }

    /**
     * Get update ratings for a volunteer
     * @param volunteerId
     * @param pageable
     * @return
     */
    @Transactional(readOnly = true)
    public Page<UpdateRatingDto> getUpdateRatingsForVolunteer(UUID volunteerId, Pageable pageable) {
        // Fetch update ratings for the volunteer
        Page<UpdateRating> ratings = updateRatingRepository.findByDonorId(volunteerId, pageable);

        // Convert to DTO
        return ratings.map(this::convertToDto);
    }

    /**
     * Get updates for a specific charity
     * @param charityId The charity ID
     * @param pageable Pagination information
     * @return Page of updates for the charity
     */
    @Transactional(readOnly = true)
    public Page<UpdateDto> getUpdatesByCharity(Long charityId, Pageable pageable) {
        log.debug("Fetching updates for charity ID: {}", charityId);
        
        // Verify charity exists
        charityRepository.findById(charityId)
                .orElseThrow(() -> new ResourceNotFoundException("Charity not found with ID: " + charityId));

        return updateRepository.findByCharityId(charityId, pageable)
                .map(this::convertToDto);
    }

    @Transactional
    public void checkAndUpdateApprovalStatus(Long updateId) {
        Update update = updateRepository.findById(updateId)
            .orElseThrow(() -> new ResourceNotFoundException("Update not found with ID: " + updateId));
            
        // Get total number of volunteers
        long totalVolunteers = userRepository.countByRoleAndIsVolunteerTrue(User.UserRole.DONOR);
        
        // Get total ratings for this update
        long totalRatings = updateRatingRepository.countByUpdateId(updateId);
        
        // Get average rating
        Double averageRating = updateRatingRepository.getAverageRatingForUpdate(updateId);
        
        // Check if all volunteers have rated and average rating is 7 or more
        if (totalRatings >= totalVolunteers && averageRating != null && averageRating >= 7.0) {
            update.setIsApproved(true);
            updateRepository.save(update);
            log.info("Update {} has been approved with average rating {}", updateId, averageRating);
        }
    }
}