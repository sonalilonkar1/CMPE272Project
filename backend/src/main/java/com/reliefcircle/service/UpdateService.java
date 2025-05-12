package com.reliefcircle.service;

import com.reliefcircle.dto.UpdateDto;
import com.reliefcircle.dto.UpdateRatingDto;
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
import java.time.LocalDateTime;

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
            
            // Check if charity should be approved
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
            
            // Check if charity should be approved
            checkAndUpdateApprovalStatus(updateId);
            
            return convertToDto(savedRating);
        }
    }
    
    /**
     * Update a rating
     */
    @Transactional
    public UpdateRatingDto updateRating(Long ratingId, UpdateRatingDto ratingDto, UUID donorId) {
        // Find existing rating by ID
        UpdateRating rating = updateRatingRepository.findById(ratingId)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Rating not found with ID: " + ratingId));

        // Verify the donor owns this rating
        if (!rating.getDonor().getId().equals(donorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Only the original donor can update this rating");
        }

        // Create rating DTO with update ID
        UpdateRatingDto updateRequest = UpdateRatingDto.builder()
            .updateId(rating.getUpdate().getId())
            .rating(ratingDto.getRating())
            .comment(ratingDto.getComment())
            .build();

        // Use existing rateUpdate method
        return rateUpdate(rating.getUpdate().getId(), updateRequest, donorId);
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
                .fileUrl(rating.getFileUrl())
                .build();
                
    }
    
    /**
     * Send notification to volunteer about a new update
     * @param volunteer The volunteer to notify
     * @param update The update details
     */
    @Transactional
    public void sendNotificationToVolunteer(User volunteer, UpdateDto update) {
        // Check if volunteer has already received any update from this charity
        if (updateRatingRepository.hasVolunteerRatedAnyUpdateForCharity(update.getCharityId(), volunteer.getId())) {
            log.debug("Volunteer {} has already received an update from charity {}. Skipping notification.", 
                volunteer.getEmail(), update.getCharityId());
            return;
        }

        // Create initial rating with file URL
        UpdateRating rating = UpdateRating.builder()
                .update(updateRepository.getReferenceById(update.getId()))
                .donor(volunteer)
                .rating(0)
                .comment(null)
                .fileUrl(update.getFileUrl())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        updateRatingRepository.save(rating);

        // Build notification message
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(String.format(
            "Hello %s,\n\nA new update has been posted for your first review of this charity.\n\n",
            volunteer.getFullName()
        ));
        
        messageBuilder.append(String.format(
            "Charity: %s\n\n",
            update.getCharityName()
        ));
        
        messageBuilder.append(String.format(
            "Update Details:\n%s\n\n",
            update.getText()
        ));

        // Add file information if available
        if (update.getFileUrl() != null && !update.getFileUrl().isEmpty()) {
            messageBuilder.append("\nSupporting Documents:\n");
            messageBuilder.append(String.format("File: %s\n", rating.getFileUrl()));
        }

        messageBuilder.append(
            "Please review and rate this update at your earliest convenience.\n\n" +
            "Best regards,\nReliefCircle Team"
        );

        // Use the NotificationService to send the notification
        notificationService.sendNotification(
            volunteer.getEmail(),
            String.format("New Update for Charity: %s - First Review Request", update.getCharityName()),
            messageBuilder.toString()
        );
        
        log.info("Sent first update notification to volunteer {} for charity {}", 
            volunteer.getEmail(), update.getCharityName());
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
            
        // Get the associated charity
        Charity charity = update.getCharity();
        
        // If charity is already verified, no need to check again
        if (charity.getIsVerified()) {
            log.debug("Charity {} is already verified, skipping verification check", charity.getId());
            return;
        }
        
        UpdateDto updateDto = convertToDto(update);
        
        // Get total number of volunteers from ratings list
        long totalVolunteers = updateDto.getRatings() != null ? updateDto.getRatings().size() : 0;
        
        // Get total ratings and average rating from DTO
        int totalRatings = updateDto.getRatingCount();
        Double averageRating = updateDto.getAverageRating();
        
        // Check if all volunteers have rated and average rating is 7 or more
        if (totalRatings >= totalVolunteers && averageRating != null && averageRating >= 7.0) {
            charity.setIsVerified(true);
            charityRepository.save(charity);
            log.info("Charity {} has been verified based on update {} with average rating {}. Total volunteers: {}", 
                charity.getId(), updateId, averageRating, totalVolunteers);
        }
    }
}