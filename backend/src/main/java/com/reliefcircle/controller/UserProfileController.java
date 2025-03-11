package com.reliefcircle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.reliefcircle.model.UserProfile;
import com.reliefcircle.service.UserProfileService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing user profiles
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserProfileController {
    
    private final UserProfileService userProfileService;
    
    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }
    
    /**
     * Retrieves all user profiles
     * 
     * @return List of user profiles
     */
    @GetMapping
    public ResponseEntity<List<UserProfile>> getUserProfiles() {
        log.info("Fetching all user profiles");
        List<UserProfile> profiles = userProfileService.getAllUserProfiles();
        return ResponseEntity.ok(profiles);
    }
    
    /**
     * Uploads a profile image for a specific user
     * 
     * @param userProfileId The ID of the user profile
     * @param file The image file to upload
     * @return Response with success/error message
     */
    @PostMapping(
            path = "/{userProfileId}/image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ImageUploadResponse> uploadUserProfileImage(
            @PathVariable("userProfileId") UUID userProfileId,
            @RequestParam("file") MultipartFile file) {
        
        log.info("Uploading profile image for user: {}", userProfileId);
        
        try {
            userProfileService.uploadUserProfileImage(userProfileId, file);
            
            ImageUploadResponse response = new ImageUploadResponse(
                    true,
                    "Image uploaded successfully"
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error uploading image for user {}: {}", userProfileId, e.getMessage(), e);
            
            ImageUploadResponse response = new ImageUploadResponse(
                    false,
                    "Failed to upload image: " + e.getMessage()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Downloads a profile image for a specific user
     * 
     * @param userProfileId The ID of the user profile
     * @return The profile image bytes with appropriate content type
     */
    @GetMapping(
            path = "/{userProfileId}/image/download",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<byte[]> downloadUserProfileImage(
            @PathVariable("userProfileId") UUID userProfileId) {
        
        log.info("Downloading profile image for user: {}", userProfileId);
        
        try {
            byte[] imageData = userProfileService.downloadUserProfileImage(userProfileId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (Exception e) {
            log.error("Error downloading image for user {}: {}", userProfileId, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Inner class for image upload responses
     */
    private static class ImageUploadResponse {
        private final boolean success;
        private final String message;
        
        public ImageUploadResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
}