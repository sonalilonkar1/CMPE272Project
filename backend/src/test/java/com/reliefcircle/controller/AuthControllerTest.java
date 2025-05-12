package com.reliefcircle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliefcircle.dto.AuthResponse;
import com.reliefcircle.dto.LoginRequest;
import com.reliefcircle.dto.RegisterRequest;
import com.reliefcircle.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void signup_WithValidRequest_ShouldReturnOk() throws Exception {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "Test User", "DONOR");
        
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnToken() throws Exception {
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AuthResponse authResponse = AuthResponse.builder()
                .token("test.jwt.token")
                .build();
        
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(authResponse.getToken()));
    }
}