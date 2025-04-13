package com.reliefcircle.util.mockdata;

import org.springframework.stereotype.Repository;
import com.reliefcircle.model.UserProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {
    private static final List<UserProfile> USER_PROFILE = new ArrayList<>();

    static {
        USER_PROFILE.add(new UserProfile(
            UUID.fromString("66bb9992-f510-47d4-b265-fc81aec6453b"), // userProfileid
            "external1", // externalId
            "test1",     // username
            "test1@example.com", // email
            "ROLE_DONOR", // role
            null         // userProfileImageLink
        ));
        USER_PROFILE.add(new UserProfile(
            UUID.fromString("968b6c12-7533-4522-8bfb-3a0020acd3c8"), // userProfileid
            "external2", // externalId
            "test2",     // username
            "test2@example.com", // email
            "ROLE_DONOR", // role
            null         // userProfileImageLink
        ));
    }

    public List<UserProfile> getUserProfile() {
        return USER_PROFILE;
    }
}