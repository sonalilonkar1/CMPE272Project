package com.reliefcircle.service;

import com.reliefcircle.dto.*;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    // Store for blacklisted tokens
    private final Set<String> tokenBlacklist = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(User.UserRole.valueOf(request.getRole()))
                .build();
        user = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .role(user.getRole())
                .build();
    }

    public AuthResponse authenticateGoogle(GoogleAuthRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());
            if (idToken != null) {
                Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");

                User user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            User newUser = User.builder()
                                    .email(email)
                                    .fullName(name)
                                    .role(User.UserRole.DONOR)
                                    .build();
                            return userRepository.save(newUser);
                        });

                var jwtToken = jwtService.generateToken(user);
                return AuthResponse.builder()
                        .token(jwtToken)
                        .role(user.getRole())
                        .build();
            }
            throw new RuntimeException("Invalid Google token");
        } catch (Exception e) {
            throw new RuntimeException("Error verifying Google token", e);
        }
    }

    /**
     * Add a token to the blacklist
     * @param token The token to blacklist
     */
    public void invalidateToken(String token) {
        tokenBlacklist.add(token);
    }

    /**
     * Check if a token is blacklisted or invalid
     * @param token The token to check
     * @return true if the token is blacklisted or invalid
     */
    public boolean isTokenInvalid(String token) {
        if (tokenBlacklist.contains(token)) {
            return true;
        }
        
        try {
            String email = jwtService.extractUsername(token);
            User user = userRepository.findByEmail(email).orElse(null);
            return user == null || !jwtService.isTokenValid(token, user);
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Clean up blacklisted tokens that are no longer valid
     * This should be called periodically
     */
    public void cleanupBlacklist() {
        tokenBlacklist.removeIf(token -> {
            try {
                String email = jwtService.extractUsername(token);
                User user = userRepository.findByEmail(email).orElse(null);
                return user == null || !jwtService.isTokenValid(token, user);
            } catch (Exception e) {
                return true;
            }
        });
    }
} 