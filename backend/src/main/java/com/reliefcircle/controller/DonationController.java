package com.reliefcircle.controller;

import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.model.UserProfile;
import com.reliefcircle.dto.VolunteerVerificationDto;
import com.reliefcircle.service.CharityService;
import com.reliefcircle.repository.UserProfileRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/donations")
public class DonationController {
    
    private final CharityService charityService;
    private final UserProfileRepository userProfileRepository;
    
    @Autowired
    public DonationController(CharityService charityService, UserProfileRepository userProfileRepository) {
        this.charityService = charityService;
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping
    public ResponseEntity<List<DonationDto>> getDonations(Authentication authentication) {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        UUID donorId = userProfileRepository.findByExternalId(oidcUser.getSubject())
            .orElseThrow(() -> new IllegalStateException("User not found")).getUserProfileid();
        List<DonationDto> donations = charityService.getDonationsForDonor(donorId);
        return ResponseEntity.ok(donations);
    }
    
    /**
     * Processes a PayPal donation and saves it to the database
     * 
     * @param paypal The PayPal donation data as a JSON string
     * @return The created donation
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DonationDto> addDonation(
            @RequestParam("paypal") String paypal,
            @RequestParam("charityId") Long charityId,
            @RequestParam(value = "volunteerOptIn", defaultValue = "false") boolean volunteerOptIn,
            Authentication authentication) {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        UserProfile donor = userProfileRepository.findByExternalId(oidcUser.getSubject())
            .orElseThrow(() -> new IllegalStateException("User not found"));
        DonationDto savedDonation = charityService.addDonation(paypal, donor.getUserProfileid(), charityId, volunteerOptIn);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDonation);
    }

    @PostMapping("/{verificationId}/verify")
    public ResponseEntity<VolunteerVerificationDto> submitVerification(
            @PathVariable("verificationId") Long verificationId,
            @RequestParam("comments") String comments,
            @RequestParam("status") String status) {
        log.info("Received verification submission for ID: {}", verificationId);

        VolunteerVerificationDto verification = charityService.submitVerification(verificationId, comments, status);
        return ResponseEntity.ok(verification);
    }
}