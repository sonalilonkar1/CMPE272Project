package com.reliefcircle.service;

import com.reliefcircle.model.UserProfile;
import com.reliefcircle.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserProfileRepository userProfileRepository;

    public CustomOAuth2UserService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        logger.debug("Starting OAuth2 user processing for client: {}", userRequest.getClientRegistration().getRegistrationId());
        
        try {
            org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService delegate = new org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService();
            OidcUser oidcUser = delegate.loadUser(userRequest);
            logger.debug("Received OidcUser: sub={}, email={}, username={}", 
                oidcUser.getSubject(), oidcUser.getEmail(), oidcUser.getPreferredUsername());

            String externalId = oidcUser.getSubject();
            Optional<UserProfile> existingUser = userProfileRepository.findByExternalId(externalId);
            logger.debug("Queried user with externalId={}: found={}", externalId, existingUser.isPresent());

            UserProfile userProfile;
            if (existingUser.isPresent()) {
                userProfile = existingUser.get();
            } else {
                userProfile = new UserProfile();
                userProfile.setUserProfileid(UUID.randomUUID());
                userProfile.setExternalId(externalId);
                userProfile.setUsername(oidcUser.getPreferredUsername() != null ? oidcUser.getPreferredUsername() : "user_" + externalId);
                userProfile.setEmail(oidcUser.getEmail() != null ? oidcUser.getEmail() : externalId + "@example.com");
                userProfile.setRole("ROLE_DONOR");
                userProfile.setUserProfileImageLink(null);
                userProfile = userProfileRepository.save(userProfile);
                logger.debug("Created new user: {}", userProfile);
            }

            return new DefaultOidcUser(
                Collections.singleton(new SimpleGrantedAuthority(userProfile.getRole())),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "sub"
            );
        } catch (Exception e) {
            logger.error("Failed to process OAuth2 user", e);
            throw e; // Re-throw to ensure Spring Security handles the failure
        }
    }
}