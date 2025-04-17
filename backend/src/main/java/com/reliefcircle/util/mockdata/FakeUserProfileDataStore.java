package com.reliefcircle.util.mockdata;

import org.springframework.stereotype.Repository;
import com.reliefcircle.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {
    private static final List<User> USER_PROFILE = new ArrayList<>();

    static {
        USER_PROFILE.add(User.builder()
            .id(UUID.fromString("66bb9992-f510-47d4-b265-fc81aec6453b"))
            .email("test1@example.com")
            .fullName("test1")
            .role(User.UserRole.DONOR)
            .build());
        USER_PROFILE.add(User.builder()
            .id(UUID.fromString("968b6c12-7533-4522-8bfb-3a0020acd3c8"))
            .email("test2@example.com")
            .fullName("test2")
            .role(User.UserRole.DONOR)
            .build());
    }

    public List<User> getUserProfile() {
        return USER_PROFILE;
    }
}