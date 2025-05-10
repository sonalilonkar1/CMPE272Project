package com.reliefcircle.controller;

import com.reliefcircle.dto.PaginatedResponse;
import com.reliefcircle.dto.PaginationRequest;
import com.reliefcircle.dto.UpdateDto;
import com.reliefcircle.dto.UpdateRatingDto;
import com.reliefcircle.model.UpdateRating;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.repository.UpdateRepository;
import com.reliefcircle.repository.UpdateRatingRepository;
import com.reliefcircle.service.UpdateService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/updates")
public class UpdateController {

    private final UpdateService updateService;
    private final UserRepository userRepository;
    private final UpdateRepository updateRepository;
    private final UpdateRatingRepository updateRatingRepository;

    /**
     * Get updates by the logged-in fundraiser
     * @param paginationRequest Pagination parameters
     * @param authentication The authentication object
     * @return Paginated list of updates created by the logged-in fundraiser
     */
    @GetMapping("/fundraiser/me")
    public ResponseEntity<PaginatedResponse<UpdateDto>> getUpdatesByLoggedInFundraiser(
            @Valid PaginationRequest paginationRequest,
            Authentication authentication
    ) {
        log.info("Fetching updates for the logged-in fundraiser");

        // Get the logged-in user
        User fundraiser;
        if (authentication.getPrincipal() instanceof User) {
            fundraiser = (User) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            fundraiser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Ensure the user is a fundraiser
        if (fundraiser.getRole() != User.UserRole.FUNDRAISER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Fetch updates created by the fundraiser
        Page<UpdateDto> page = updateService.getUpdatesByFundraiser(
                fundraiser.getId(),
                PageRequest.of(
                        paginationRequest.getPageNumber(),
                        paginationRequest.getPageSize()
                )
        );

        // Build the paginated response
        PaginatedResponse<UpdateDto> response = PaginatedResponse.<UpdateDto>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    
    /**
     * Get update ratings for the logged-in volunteer
     * @param paginationRequest Pagination parameters
     * @param authentication The authentication object
     * @return Paginated list of update ratings for the logged-in volunteer
     */
    @GetMapping("/volunteer/me/ratings")
    public ResponseEntity<PaginatedResponse<UpdateRatingDto>> getUpdateRatingsForLoggedInVolunteer(
            @Valid PaginationRequest paginationRequest,
            Authentication authentication
    ) {
        log.info("Fetching update ratings for the logged-in volunteer");

        // Get the logged-in user
        User volunteer;
        if (authentication.getPrincipal() instanceof User) {
            volunteer = (User) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            volunteer = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Ensure the user is a volunteer
        if (volunteer.getRole() != User.UserRole.VOLUNTEER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Fetch update ratings for the volunteer
        Page<UpdateRatingDto> page = updateService.getUpdateRatingsForVolunteer(
                volunteer.getId(),
                PageRequest.of(
                        paginationRequest.getPageNumber(),
                        paginationRequest.getPageSize()
                )
        );

        // Build the paginated response
        PaginatedResponse<UpdateRatingDto> response = PaginatedResponse.<UpdateRatingDto>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

        return ResponseEntity.ok(response);
    }



    /**
     * Get a specific update by ID
     * @param id The update ID
     * @return The update with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UpdateDto> getUpdateById(
        @PathVariable("id") @Positive Long id
    ) {
        log.info("Fetching update with ID: {}", id);
        return ResponseEntity.ok(updateService.getUpdateById(id));
    }

    /**
     * Create a new update
     * @param charityId The charity ID
     * @param text The update text content
     * @param file Optional file attachment
     * @param authentication The authentication object
     * @return The created update
     */
    @PostMapping(
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UpdateDto> createUpdate(
        @RequestParam("charityId") @Positive Long charityId,
        @RequestParam("text") @NotBlank String text,
        @RequestParam(value = "file", required = false) MultipartFile file,
        Authentication authentication
    ) {
        log.info("Creating update for charity: {}", charityId);

        // Get fundraiser from authentication
        User fundraiser;
        if (authentication.getPrincipal() instanceof User) {
            fundraiser = (User) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();
            fundraiser = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Only fundraisers can create updates
        if (fundraiser.getRole() != User.UserRole.FUNDRAISER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Create update DTO
        UpdateDto updateDto = UpdateDto.builder()
            .charityId(charityId)
            .fundraiserId(fundraiser.getId())
            .text(text)
            .file(file)
            .build();

        // Save the update
        UpdateDto createdUpdate = updateService.createUpdate(updateDto);

        // Notify 10 random donors who are volunteers and have not donated to this charity
        notifyRandomVolunteers(charityId, createdUpdate);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUpdate);
        
    }

    /**
     * Delete an update
     * @param id The update ID
     * @param authentication The authentication object
     * @return No content on success
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUpdate(
        @PathVariable("id") @Positive Long id,
        Authentication authentication
    ) {
        log.info("Deleting update with ID: {}", id);

        // Get user from authentication
        User user;
        if (authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();
            user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Delete the update
        updateService.deleteUpdate(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Rate an update
     * @param updateId The update ID
     * @param rating The rating value (1-5)
     * @param comment Optional comment to accompany the rating
     * @param authentication The authentication object
     * @return The created rating
     */
    @PostMapping("/{updateId}/rate")
    public ResponseEntity<UpdateRatingDto> rateUpdate(
        @PathVariable("updateId") @Positive Long updateId,
        @RequestParam("rating") @Positive Integer rating,
        @RequestParam(value = "comment", required = false) String comment,
        Authentication authentication
    ) {
        log.info("Rating update with ID: {} (rating: {})", updateId, rating);

        // Get donor from authentication
        User donor;
        if (authentication.getPrincipal() instanceof User) {
            donor = (User) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();
            donor = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Only donors can rate updates
        if (donor.getRole() != User.UserRole.DONOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Validate rating value
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(null);
        }

        // Create rating DTO
        UpdateRatingDto ratingDto = UpdateRatingDto.builder()
            .updateId(updateId)
            .rating(rating)
            .comment(comment)
            .build();

        // Save the rating
        UpdateRatingDto createdRating = updateService.rateUpdate(
            updateId,
            ratingDto,
            donor.getId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }

    /**
     * Get ratings for an update
     * @param updateId The update ID
     * @param paginationRequest Pagination parameters
     * @return Paginated list of ratings for the update
     */
    @GetMapping("/{updateId}/ratings")
    public ResponseEntity<
        PaginatedResponse<UpdateRatingDto>
    > getRatingsForUpdate(
        @PathVariable("updateId") @Positive Long updateId,
        @Valid PaginationRequest paginationRequest
    ) {
        log.info("Fetching ratings for update: {}", updateId);

        Page<UpdateRatingDto> page = updateService.getRatingsForUpdate(
            updateId,
            PageRequest.of(
                paginationRequest.getPageNumber(),
                paginationRequest.getPageSize()
            )
        );

        PaginatedResponse<UpdateRatingDto> response = PaginatedResponse.<
                UpdateRatingDto
            >builder()
            .content(page.getContent())
            .pageNumber(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .last(page.isLast())
            .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a rating
     * @param ratingId The rating ID
     * @param authentication The authentication object
     * @return No content on success
     */
    @DeleteMapping("/ratings/{ratingId}")
    public ResponseEntity<Void> deleteRating(
        @PathVariable("ratingId") @Positive Long ratingId,
        Authentication authentication
    ) {
        log.info("Deleting rating with ID: {}", ratingId);

        // Get user from authentication
        User user;
        if (authentication.getPrincipal() instanceof User) {
            user = (User) authentication.getPrincipal();
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();
            user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Delete the rating
        updateService.deleteRating(ratingId, user.getId());
        return ResponseEntity.noContent().build();
    }

    private void notifyRandomVolunteers(Long charityId, UpdateDto createdUpdate) {
    log.info("Notifying 10 random volunteers for charity ID: {}", charityId);

    // Fetch 10 random volunteers who have not donated to this charity
    List<User> volunteers = userRepository.findRandomVolunteersNotDonatedToCharity(charityId, 10);

    // Send notifications to the selected volunteers
    for (User volunteer : volunteers) {
        log.info("Notifying volunteer: {}", volunteer.getEmail());

         // Add an entry to update_ratings for the volunteer
        UpdateRating updateRating = UpdateRating.builder()
                .update(updateRepository.findById(createdUpdate.getId())
                        .orElseThrow(() -> new RuntimeException("Update not found with ID: " + createdUpdate.getId())))
                .donor(volunteer)
                .rating(0) // Default rating (e.g., null if not rated yet)
                .comment(null) // No comment initially
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        updateRatingRepository.save(updateRating);

        // Implement your notification logic here (e.g., email, SMS, push notification)
        updateService.sendNotificationToVolunteer(volunteer, createdUpdate);
    }
}
}
