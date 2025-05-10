package com.reliefcircle.dto;

import com.reliefcircle.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String externalId;
    private String email;
    private String fullName;
    private String phoneNumber;
    private User.UserRole role;
    private Boolean isVolunteer;
    private String userProfileImageLink;
    private LocalDateTime createdAt;

    /**
     * Convert User entity to UserDto
     * @param user The user entity
     * @return UserDto without sensitive information
     */
    public static UserDto fromUser(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .externalId(user.getExternalId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isVolunteer(user.getIsVolunteer())
                .userProfileImageLink(user.getUserProfileImageLink().orElse(null))
                .createdAt(user.getCreatedAt())
                .build();
    }
}