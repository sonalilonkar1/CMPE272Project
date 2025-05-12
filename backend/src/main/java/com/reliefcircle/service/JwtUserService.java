package com.reliefcircle.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtUserService {

    public UUID getUserIdFromToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            
            // This assumes the user ID is stored in the 'sub' claim
            String userId = jwt.getSubject();
            return UUID.fromString(userId);
        }
        throw new IllegalArgumentException("Invalid authentication type");
    }

    public String getEmailFromToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwt.getClaimAsString("email");
        }
        throw new IllegalArgumentException("Invalid authentication type");
    }

    public String getNameFromToken(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwt.getClaimAsString("name");
        }
        throw new IllegalArgumentException("Invalid authentication type");
    }
} 