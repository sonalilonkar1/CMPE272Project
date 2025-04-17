package com.reliefcircle.controller;

import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.service.JwtUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUserService jwtUserService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {
        try {
            logger.debug("Authentication type: {}", authentication.getClass().getName());
            logger.debug("Authentication principal: {}", authentication.getPrincipal());
            
            if (authentication.getPrincipal() instanceof OidcUser) {
                OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                logger.debug("OIDC User subject: {}", oidcUser.getSubject());
                
                return userRepository.findByExternalId(oidcUser.getSubject())
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
            } else {
                logger.warn("Unexpected authentication type: {}", authentication.getClass().getName());
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return ResponseEntity.badRequest().build();
        }
    }
}