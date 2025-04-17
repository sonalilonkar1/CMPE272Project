package com.reliefcircle.controller;

import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.model.User;
import com.reliefcircle.dto.VerificationDto;
import com.reliefcircle.service.CharityService;
import com.reliefcircle.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
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
     * @return List of donations based on the provided filter
     */
    @GetMapping
    public ResponseEntity<List<DonationDto>> getDonations(
            @RequestParam(required = false) UUID donorId) {
        
        if (donorId != null) {
            return ResponseEntity.ok(charityService.getDonationsForDonor(donorId));
        } else {
            return ResponseEntity.ok(charityService.getDonations());
        }
    }
    
    /**
     * Processes a PayPal donation and saves it to the database
     * 
     * @param request The donation request data as a JSON object
     * @return The created donation
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DonationDto> addDonation(
            @RequestBody DonationDto request,
            Authentication authentication) {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        User donor = UserRepository.findByExternalId(oidcUser.getSubject())
            .orElseThrow(() -> new IllegalStateException("User not found"));
            
        request.setDonorId(donor.getId());
        DonationDto savedDonation = charityService.addDonation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDonation);
    }

    @PostMapping("/{verificationId}/verify")
    public ResponseEntity<VerificationDto> submitVerification(
            @PathVariable("verificationId") Long verificationId,
            @RequestParam("comments") String comments,
            @RequestParam("status") String status) {
        log.info("Received verification submission for ID: {}", verificationId);

        VerificationDto verification = charityService.submitVerification(verificationId, comments, status);
        return ResponseEntity.ok(verification);
    }
}