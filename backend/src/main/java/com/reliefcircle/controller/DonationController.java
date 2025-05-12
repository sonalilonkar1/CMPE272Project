package com.reliefcircle.controller;

import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.dto.PaginatedResponse;
import com.reliefcircle.dto.PaginationRequest;
import com.reliefcircle.model.User;
import com.reliefcircle.dto.VerificationDto;
import com.reliefcircle.service.CharityService;
import com.reliefcircle.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/donations")
public class DonationController {
    
    private final CharityService charityService;
    private final UserRepository UserRepository;
    
    @Autowired
    public DonationController(CharityService charityService, UserRepository UserRepository) {
        this.charityService = charityService;
        this.UserRepository = UserRepository;
    }

    /**
     * Get all donations with optional filtering by donorId
     * @param donorId Optional donor ID to filter donations
     * @param paginationRequest Pagination parameters
     * @return Paginated list of donations based on the provided filter
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<DonationDto>> getDonations(
            @RequestParam(required = false) UUID donorId,
            @Valid PaginationRequest paginationRequest) {
        
        Page<DonationDto> page;
        if (donorId != null) {
            page = charityService.getDonationsForDonor(donorId, 
                PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize()));
        } else {
            page = charityService.getDonations(
                PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize()));
        }

        PaginatedResponse<DonationDto> response = PaginatedResponse.<DonationDto>builder()
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
     * Processes a PayPal donation and saves it to the database
     * 
     * @param request The donation request data as a JSON object
     * @return The created donation
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DonationDto> addDonation(
            @Valid @RequestBody DonationDto request) {
        
        User donor = UserRepository.findById(request.getDonorId())
            .orElseThrow(() -> new IllegalStateException("User not found"));
            
        request.setDonorId(donor.getId());
        DonationDto savedDonation = charityService.addDonation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDonation);
    }

    @PostMapping("/{verificationId}/verify")
    public ResponseEntity<VerificationDto> submitVerification(
            @PathVariable("verificationId") @Positive(message = "Verification ID must be positive") Long verificationId,
            @RequestParam("comments") @NotBlank(message = "Comments are required") String comments,
            @RequestParam("status") @NotBlank(message = "Status is required") String status,
            Authentication authentication) {
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new IllegalArgumentException("Invalid authentication type");
        }

        // Check if user is a donor and volunteer, or a fundraiser
        if ((user.getRole() != User.UserRole.DONOR || !user.getIsVolunteer()) && user.getRole() != User.UserRole.FUNDRAISER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        VerificationDto verification = charityService.submitVerification(verificationId, comments, status);
        return ResponseEntity.ok(verification);
    }
}