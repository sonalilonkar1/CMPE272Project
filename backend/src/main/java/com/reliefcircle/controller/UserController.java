package com.reliefcircle.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;

import com.reliefcircle.model.User;
import com.reliefcircle.service.UserService;
import com.reliefcircle.service.JwtUserService;
import com.reliefcircle.service.CharityService;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.dto.PaginationRequest;
import com.reliefcircle.dto.PaginatedResponse;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import jakarta.validation.Valid;

/**
 * REST controller for managing user profiles
 */
@Validated
@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService UserService;
    private final JwtUserService jwtUserService;
    private final UserRepository userRepository;
    private final CharityService charityService;
    
    @Autowired
    public UserController(UserService UserService, JwtUserService jwtUserService, 
                         UserRepository userRepository, CharityService charityService) {
        this.UserService = UserService;
        this.jwtUserService = jwtUserService;
        this.userRepository = userRepository;
        this.charityService = charityService;
    }
    
    /**
     * Retrieves all user profiles with pagination
     * 
     * @param paginationRequest Pagination parameters
     * @param authentication The authentication object
     * @return Paginated list of user profiles
     */
    @GetMapping
    public ResponseEntity<PaginatedResponse<User>> getUsers(
            @Valid PaginationRequest paginationRequest,
            Authentication authentication) {
        log.info("Fetching all user profiles");
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            if (user.getRole() != User.UserRole.FUNDRAISER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Page<User> page = UserService.getAllUsers(
                PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize()));

            PaginatedResponse<User> response = PaginatedResponse.<User>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

            return ResponseEntity.ok(response);
        } else {
            throw new IllegalArgumentException("Invalid authentication type");
        }
    }

    /**
     * Get a user by their ID
     * 
     * @param id The user ID
     * @param authentication The authentication object
     * @return The user profile
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @PathVariable("id") UUID id,
            Authentication authentication) {
        log.info("Fetching user profile with ID: {}", id);
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            // Only allow users to view their own profile or fundraisers to view any profile
            if (!user.getId().equals(id) && user.getRole() != User.UserRole.FUNDRAISER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            User foundUser = UserService.getUserById(id);
            return ResponseEntity.ok(foundUser);
        } else {
            throw new IllegalArgumentException("Invalid authentication type");
        }
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
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        log.info("Uploading profile image for user: {}", userId);
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new IllegalArgumentException("Invalid authentication type");
        }

        // Only allow users to upload their own image
        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
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
            @PathVariable("userId") UUID userId,
            Authentication authentication) {
        
        log.info("Downloading profile image for user: {}", userId);
        
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User user)) {
            throw new IllegalArgumentException("Invalid authentication type");
        }

        // Only allow users to download their own image
        if (!user.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
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
     * Get list of donors who are volunteers
     * @param paginationRequest Pagination parameters
     * @return Paginated list of donors who are volunteers
     */
    @GetMapping("/donors/volunteers")
    public ResponseEntity<PaginatedResponse<User>> getDonorsWhoAreVolunteers(
            @Valid PaginationRequest paginationRequest,
            Authentication authentication) {
        log.info("Fetching list of donors who are volunteers");
        
        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            if (user.getRole() != User.UserRole.FUNDRAISER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Page<User> page = charityService.getDonorsWhoAreVolunteers(
                PageRequest.of(paginationRequest.getPageNumber(), paginationRequest.getPageSize()));

            PaginatedResponse<User> response = PaginatedResponse.<User>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

            return ResponseEntity.ok(response);
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