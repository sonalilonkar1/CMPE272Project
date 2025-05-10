package com.reliefcircle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDto {
    private Long id;
    private UUID fundraiserId;
    private String fundraiserName;
    private String fundraiserEmail;
    private Long charityId;
    private String charityName;
    private String text;
    private String fileUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double averageRating;
    private Integer ratingCount;
    
    // Used for file uploads
    private MultipartFile file;
    
    // Ratings details
    private List<UpdateRatingDto> ratings;
    
    // User's own rating if they've rated this update
    private UpdateRatingDto userRating;
}