package com.reliefcircle.service;

import com.reliefcircle.model.User;
import com.reliefcircle.model.User.UserRole;
import com.reliefcircle.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
            Optional<User> existingUser = userRepository.findByExternalId(externalId);
            logger.debug("Queried user with externalId={}: found={}", externalId, existingUser.isPresent());

            User user;
            if (existingUser.isPresent()) {
                user = existingUser.get();
            } else {
                // Accept all email domains - no restrictions
                user = User.builder()
                    .id(UUID.randomUUID())
                    .externalId(externalId)
                    .email(oidcUser.getEmail() != null ? oidcUser.getEmail() : externalId + "@example.com")
                    .fullName(oidcUser.getPreferredUsername() != null ? oidcUser.getPreferredUsername() : "user_" + externalId)
                    .role(UserRole.DONOR)
                    .passwordHash("OAUTH2_USER") // Temporary password for OAuth2 users
                    .build();
                user = userRepository.save(user);
                logger.debug("Created new user: {}", user);
            }

            // Grant admin scope to the authenticated user
            if (user.getEmail().equals("sonali.lonkar@sjsu.edu")) {
                return new DefaultOidcUser(
                    Arrays.asList(
                        new SimpleGrantedAuthority("SCOPE_admin"),
                        new SimpleGrantedAuthority(UserRole.FUNDRAISER.name())
                    ),
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo(),
                    "sub"
                );
            }

            return new DefaultOidcUser(
                Collections.singleton(new SimpleGrantedAuthority(UserRole.FUNDRAISER.name())),
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