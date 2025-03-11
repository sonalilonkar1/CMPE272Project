package com.reliefcircle.controller;

import com.reliefcircle.dto.DonationDto;
import com.reliefcircle.service.CharityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/donations")
public class DonationController {
    
    private final CharityService charityService;
    
    @Autowired
    public DonationController(CharityService charityService) {
        this.charityService = charityService;
    }
    
    /**
     * Processes a PayPal donation and saves it to the database
     * 
     * @param paypal The PayPal donation data as a JSON string
     * @return The created donation
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DonationDto> addDonation(@RequestParam("paypal") String paypal) {
        log.info("Received donation request with PayPal data");
        
        // The service returns the saved DonationDto directly
        DonationDto savedDonation = charityService.addDonation(paypal);
        
        // Return the DTO with a 201 Created status code
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDonation);
    }
}