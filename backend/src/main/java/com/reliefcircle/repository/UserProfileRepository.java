package com.reliefcircle.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.reliefcircle.model.UserProfile;
import com.reliefcircle.util.mockdata.FakeUserProfileDataStore;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserProfileRepository {
    private final FakeUserProfileDataStore fakeUserProfileDataStore;

    @Autowired
    public UserProfileRepository(FakeUserProfileDataStore fakeUserProfileDataStore) {
        this.fakeUserProfileDataStore = fakeUserProfileDataStore;
    }

    public List<UserProfile> findAll() {
        return fakeUserProfileDataStore.getUserProfile();
    }
    
    public Optional<UserProfile> findById(UUID userProfileId) {
        return fakeUserProfileDataStore.getUserProfile()
                .stream()
                .filter(userProfile -> userProfile.getUserProfileid().equals(userProfileId))
                .findFirst();
    }
    
    public UserProfile save(UserProfile userProfile) {
        // First, remove the user if it exists (to update it)
        fakeUserProfileDataStore.getUserProfile().removeIf(
            existingUser -> existingUser.getUserProfileid().equals(userProfile.getUserProfileid())
        );
        
        // Then add the updated/new user
        fakeUserProfileDataStore.getUserProfile().add(userProfile);
        
        return userProfile;
    }
}