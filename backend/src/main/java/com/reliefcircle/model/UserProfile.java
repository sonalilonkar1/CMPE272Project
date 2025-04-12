package com.reliefcircle.model;

import javax.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @Column(name = "user_profile_id")
    private UUID userProfileid;

    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId; // e.g., Google's "sub" ID

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role", nullable = false)
    private String role; // e.g., "ROLE_DONOR", "ROLE_ADMIN"

    @Column(name = "user_profile_image_link")
    private String userProfileImageLink;

    public Optional<String> getUserProfileImageLink() {
        return Optional.ofNullable(userProfileImageLink);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfile that = (UserProfile) o;
        return Objects.equals(userProfileid, that.userProfileid) &&
                Objects.equals(externalId, that.externalId) &&
                Objects.equals(username, that.username) &&
                Objects.equals(email, that.email) &&
                Objects.equals(role, that.role) &&
                Objects.equals(userProfileImageLink, that.userProfileImageLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfileid, externalId, username, email, role, userProfileImageLink);
    }
}