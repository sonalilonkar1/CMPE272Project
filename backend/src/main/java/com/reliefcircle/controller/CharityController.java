package com.reliefcircle.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.exception.CharityException;
import com.reliefcircle.service.CharityService;

@RestController
@RequestMapping("/api/charities")
@CrossOrigin(origins = "*")
public class CharityController {
    
    private final CharityService charityService;
    
    @Autowired
    public CharityController(CharityService charityService) {
        this.charityService = charityService;
    }
    
    /**
     * Get all charities with optional filtering by donorId or fundraiserId
     * @param donorId Optional donor ID to filter charities by donations
     * @param fundraiserId Optional fundraiser ID to filter charities by fundraiser
     * @return List of charities based on the provided filters
     */
    @GetMapping
    public ResponseEntity<List<CharityDto>> getAllCharities(
            @RequestParam(required = false) UUID donorId,
            @RequestParam(required = false) UUID fundraiserId) {
        
        if (donorId != null) {
            return ResponseEntity.ok(charityService.getCharitiesByDonor(donorId));
        } else if (fundraiserId != null) {
            return ResponseEntity.ok(charityService.getCharitiesByFundraiser(fundraiserId));
        } else {
            return ResponseEntity.ok(charityService.getAllCharities());
        }
    }
    
    /**
     * Get only verified charities
     * @return List of verified charities
     */
    @GetMapping("/verified")
    public ResponseEntity<List<CharityDto>> getVerifiedCharities() {
        return ResponseEntity.ok(charityService.getVerifiedCharities());
    }
    
    /**
     * Get a charity by ID
     * @param id Charity ID
     * @return The charity with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CharityDto> getCharityById(@PathVariable("id") long id) {
        return ResponseEntity.ok(charityService.getCharityById(id));
    }
    
    /**
     * Verify a charity by ID
     * @param id Charity ID
     * @return Success/failure response
     */
    @PutMapping("/{id}/verify")
    public ResponseEntity<String> verifyCharity(@PathVariable("id") long id) {
        boolean success = charityService.verifyCharity(id);
        if (success) {
            return ResponseEntity.ok("Charity verified successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Charity not found or could not be verified");
        }
    }
    
    /**
     * Register a new charity
     * @param name Charity name
     * @param description Charity description
     * @param organizationName Charity organization name
     * @param category Charity category
     * @param targetAmount Charity target amount
     * @param file Logo or image file
     * @return The created charity
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CharityDto> registerCharity(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "organizationName", required = false) String organizationName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "targetAmount", required = false) BigDecimal targetAmount,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        try {
            CharityDto charityRequest = CharityDto.builder()
                    .name(name)
                    .description(description)
                    .organizationName(organizationName)
                    .category(category)
                    .targetAmount(targetAmount)
                    .file(file)
                    .build();
            
            CharityDto createdCharity = charityService.registerCharity(charityRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCharity);
        } catch (Exception ex) {
            throw new CharityException("Failed to register charity: " + ex.getMessage(), ex);
        }
    }
}