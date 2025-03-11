package com.reliefcircle.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.reliefcircle.exception.EmptyFileException;
import com.reliefcircle.exception.ResourceNotFoundException;
import com.reliefcircle.model.UserProfile;
import com.reliefcircle.repository.UserProfileRepository;
import com.reliefcircle.util.aws.BucketName;
import com.reliefcircle.util.aws.FileStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final FileStore fileStore;

    public List<UserProfile> getAllUserProfiles() {
        return userProfileRepository.findAll();
    }

    public UserProfile getUserProfileById(UUID userProfileId) {
        return userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User profile not found with ID: %s", userProfileId)));
    }
    
    public void uploadUserProfileImage(UUID userProfileId, MultipartFile file) {
        // Validate inputs
        Objects.requireNonNull(userProfileId, "User profile ID cannot be null");
        validateFile(file);

        try {
            // Get user profile
            UserProfile userProfile = getUserProfileById(userProfileId);
            
            // Prepare upload details
            Map<String, String> metadata = extractMetadata(file);
            String path = buildS3FolderPath(userProfile);
            Optional<String> filename = generateUniqueFilename(file.getOriginalFilename());
            
            if (!filename.isPresent()) {
                log.error("Failed to generate filename for user: {}", userProfileId);
                throw new RuntimeException("Failed to generate filename for upload");
            }
            
            // Upload to S3
            fileStore.save(path, filename.get(), Optional.of(metadata), file.getInputStream());
            
            // Update user profile with image link
            userProfile.setUserProfileImageLink(filename.get());
            userProfileRepository.save(userProfile);
            
            log.info("Successfully uploaded profile image for user: {}", userProfileId);
        } catch (ResourceNotFoundException e) {
            log.error("User profile not found for image upload: {}", userProfileId);
            throw e;
        } catch (IOException e) {
            log.error("Failed to read file stream during profile image upload for user: {}", userProfileId, e);
            throw new RuntimeException("Failed to process the uploaded file", e);
        } catch (Exception e) {
            log.error("Unexpected error during profile image upload for user: {}", userProfileId, e);
            throw new RuntimeException("Failed to upload profile image", e);
        }
    }
    
    public byte[] downloadUserProfileImage(UUID userProfileId) {
        UserProfile userProfile = getUserProfileById(userProfileId);
        
        // Handle Optional<String> correctly
        Optional<String> imageLinkOptional = userProfile.getUserProfileImageLink();
        if (!imageLinkOptional.isPresent()) {
            log.info("No profile image found for user: {}", userProfileId);
            return new byte[0];
        }
        
        String imageLink = imageLinkOptional.get();
        String path = buildS3FolderPath(userProfile);
        
        try {
            byte[] imageData = fileStore.download(path, imageLink);
            log.info("Successfully downloaded profile image for user: {}", userProfileId);
            return imageData;
        } catch (Exception e) {
            log.error("Failed to download profile image for user: {}", userProfileId, e);
            throw new IllegalStateException("Failed to download profile image", e);
        }
    }

    // Helper methods
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("Cannot upload empty file");
        }
        
        // Add additional validations if needed (file size, type, etc.)
    }
    
    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }
    
    private String buildS3FolderPath(UserProfile userProfile) {
        return String.format("%s/%s", 
                BucketName.PROFILE_IMAGE.getBucketName(), 
                userProfile.getUserProfileid());
    }
    
    private Optional<String> generateUniqueFilename(String originalFilename) {
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sanitizedFilename = originalFilename.replace(" ", "_").toLowerCase();
        String uniqueFilename = String.format("%s-%s", sanitizedFilename, UUID.randomUUID());
        return Optional.of(uniqueFilename);
    }
}