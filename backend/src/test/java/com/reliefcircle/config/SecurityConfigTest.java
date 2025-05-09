package com.reliefcircle.config;

import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SecurityConfigTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private HttpSecurity httpSecurity;

    private SecurityConfig securityConfig;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityConfig = new SecurityConfig(userRepository, jwtService);
        
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("password")
                .role(User.UserRole.FUNDRAISER)
                .build();
    }

    @Test
    void testUserDetailsService_UserFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertNotNull(userDetails);
        assertEquals(testUser.getEmail(), userDetails.getUsername());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testUserDetailsService_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        assertThrows(UsernameNotFoundException.class, () -> 
            userDetailsService.loadUserByUsername("nonexistent@example.com"));
    }

    @Test
    void testPasswordEncoder_EncodesPassword() {
        // Act
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();
        String encodedPassword = passwordEncoder.encode("testPassword");

        // Assert
        assertNotNull(encodedPassword);
        assertNotEquals("testPassword", encodedPassword);
        assertTrue(passwordEncoder.matches("testPassword", encodedPassword));
    }

    @Test
    void testAuthenticationProvider_ConfiguresCorrectly() {
        // Arrange
        UserDetailsService userDetailsService = securityConfig.userDetailsService();
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        // Act
        AuthenticationProvider authProvider = securityConfig.authenticationProvider(userDetailsService, passwordEncoder);

        // Assert
        assertNotNull(authProvider);
        assertTrue(authProvider instanceof DaoAuthenticationProvider);
    }

    @Test
    void testJwtAuthenticationFilter_CreatedWithCorrectDependencies() {
        
        // Act
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter();

        // Assert
        assertNotNull(filter);
        // The filter should be created with the correct dependencies
        assertNotNull(ReflectionTestUtils.getField(filter, "jwtService"));
        assertNotNull(ReflectionTestUtils.getField(filter, "userDetailsService"));
    }

    @Test
    void testSecurityFilterChain_ConfiguresCorrectly() throws Exception {
        // Arrange
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        when(httpSecurity.build()).thenReturn(mock(DefaultSecurityFilterChain.class));

        // Act
        SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

        // Assert
        assertNotNull(filterChain);
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authenticationProvider(any());
        verify(httpSecurity).addFilterBefore(any(), any());
    }

    @Test
    void testAuthenticationManager_ReturnsCorrectManager() throws Exception {
        // Arrange
        AuthenticationManager expectedManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(expectedManager);

        // Act
        AuthenticationManager actualManager = securityConfig.authenticationManager(authenticationConfiguration);

        // Assert
        assertNotNull(actualManager);
        assertEquals(expectedManager, actualManager);
    }

    @Test
    void testAuthenticationManager_ThrowsException() throws Exception {
        // Arrange
        when(authenticationConfiguration.getAuthenticationManager())
            .thenThrow(new Exception("Authentication configuration error"));

        // Act & Assert
        assertThrows(Exception.class, () -> 
            securityConfig.authenticationManager(authenticationConfiguration));
    }
} 