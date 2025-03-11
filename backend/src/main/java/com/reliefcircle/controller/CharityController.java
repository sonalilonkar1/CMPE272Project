package com.reliefcircle.controller;

import java.util.List;

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
     * Get all charities
     * @return List of all charities
     */
    @GetMapping
    public ResponseEntity<List<CharityDto>> getAllCharities() {
        return ResponseEntity.ok(charityService.getAllCharities());
    }
    
    /**
     * Get only approved charities
     * @return List of approved charities
     */
    @GetMapping("/approved")
    public ResponseEntity<List<CharityDto>> getApprovedCharities() {
        return ResponseEntity.ok(charityService.getApprovedCharities());
    }
    
    /**
     * Approve a charity by ID
     * @param id Charity ID
     * @return Success/failure response
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<String> approveCharity(@PathVariable("id") long id) {
        boolean success = charityService.approveCharity(id);
        if (success) {
            return ResponseEntity.ok("Charity approved successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Charity not found or could not be approved");
        }
    }
    
    /**
     * Register a new charity
     * @param cname Charity name
     * @param location Charity location
     * @param email Contact email
     * @param description Charity description
     * @param file Logo or image file
     * @return The created charity
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CharityDto> registerCharity(
            @RequestParam("cname") String cname,
            @RequestParam("location") String location,
            @RequestParam("email") String email,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file) {
        
        try {
            CharityDto charityRequest = CharityDto.builder()
                    .cname(cname)
                    .location(location)
                    .email(email)
                    .description(description)
                    .file(file)
                    .build();
            
            CharityDto createdCharity = charityService.registerCharity(charityRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCharity);
        } catch (Exception ex) {
            throw new CharityException("Failed to register charity: " + ex.getMessage(), ex);
        }
    }
}