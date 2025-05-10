package com.reliefcircle.service;

import com.reliefcircle.model.User;
import com.reliefcircle.model.User.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 86400000L); // 24 hours

        testUser = User.builder()
                .email("test@example.com")
                .fullName("Test User")
                .role(UserRole.FUNDRAISER)
                .build();
    }

    @Test
    void testGenerateAndValidateToken() {
        // Generate token
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);

        // Extract username
        String username = jwtService.extractUsername(token);
        assertEquals(testUser.getEmail(), username);

        // Validate token
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    void testTokenClaims() {
        String token = jwtService.generateToken(testUser);
        Map<String, Object> claims = jwtService.getAllClaims(token);
        
        assertNotNull(claims);
        assertEquals(testUser.getEmail(), claims.get("sub"));
        assertEquals(UserRole.FUNDRAISER.name(), claims.get("role"));
    }

    @Test
    void testInvalidToken() {
        // Create a token with a different user
        User differentUser = User.builder()
                .email("different@example.com")
                .fullName("Different User")
                .role(UserRole.FUNDRAISER)
                .passwordHash("password")
                .build();
        String token = jwtService.generateToken(differentUser);
        
        // The token should be invalid for the test user
        assertFalse(jwtService.isTokenValid(token, testUser));
    }
} 