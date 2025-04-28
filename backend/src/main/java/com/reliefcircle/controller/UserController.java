package com.reliefcircle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;

import com.reliefcircle.model.User;
import com.reliefcircle.service.UserService;
import com.reliefcircle.service.JwtUserService;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing user profiles
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService UserService;
    private final JwtUserService jwtUserService;
    
    @Autowired
    public UserController(UserService UserService, JwtUserService jwtUserService) {
        this.UserService = UserService;
        this.jwtUserService = jwtUserService;
    }
    
    /**
     * Retrieves all user profiles
     * 
     * @return List of user profiles
     */
    @GetMapping
    public ResponseEntity<List<User>> getUsers() {
        log.info("Fetching all user profiles");
        List<User> profiles = UserService.getAllUsers();
        return ResponseEntity.ok(profiles);
    }
    
    /**
     * Uploads a profile image for a specific user
     * 
     * @param userId The ID of the user profile
     * @param file The image file to upload
     * @return Response with success/error message
     */
    @PostMapping(
            path = "/{userId}/image/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ImageUploadResponse> uploadUserImage(
            @PathVariable("userId") UUID userId,
            @RequestParam("file") MultipartFile file) {
        
        log.info("Uploading profile image for user: {}", userId);
        
        try {
            UserService.uploadUserImage(userId, file);
            
            ImageUploadResponse response = new ImageUploadResponse(
                    true,
                    "Image uploaded successfully"
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error uploading image for user {}: {}", userId, e.getMessage(), e);
            
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
     * @param userId The ID of the user profile
     * @return The profile image bytes with appropriate content type
     */
    @GetMapping(
            path = "/{userId}/image/download",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<byte[]> downloadUserImage(
            @PathVariable("userId") UUID userId) {
        
        log.info("Downloading profile image for user: {}", userId);
        
        try {
            byte[] imageData = UserService.downloadUserImage(userId);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (Exception e) {
            log.error("Error downloading image for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get the current user's information
     * 
     * @param authentication The authentication object containing the JWT token
     * @return The current user's information
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        log.info("Fetching current user information");

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return ResponseEntity.ok(user);
        } else {
            throw new IllegalArgumentException("Invalid authentication type");
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