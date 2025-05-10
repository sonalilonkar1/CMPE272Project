package com.reliefcircle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliefcircle.dto.PaginationRequest;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.service.UserService;
import com.reliefcircle.service.JwtUserService;
import com.reliefcircle.service.CharityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUserService jwtUserService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CharityService charityService;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        UserController controller = new UserController(userService, jwtUserService, userRepository, charityService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .role(User.UserRole.FUNDRAISER)  // Set as FUNDRAISER to access endpoints
                .build();

        userDetails = new org.springframework.security.core.userdetails.User(
                testUser.getEmail(),
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_FUNDRAISER"))
        );

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    }

    @Test
    void testGetAllUsers() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("email").ascending());
        Page<User> page = new PageImpl<>(Arrays.asList(testUser));
        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PaginationRequest(0, 10, "email", "asc")))
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].email").value("test@example.com"));
    }

    @Test
    void testGetUserById() throws Exception {
        // Arrange
        when(userService.getUserById(any(UUID.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUploadUserImage() throws Exception {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/users/{userId}/image/upload", testUser.getId())
                .file(file)
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isCreated())  // Changed from isOk() to isCreated()
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGetCurrentUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/me")
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testGetDonorsAndVolunteers() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("email").ascending());
        Page<User> page = new PageImpl<>(Arrays.asList(testUser));
        when(charityService.getDonorsWhoAreVolunteers(any(PageRequest.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/users/donors/volunteers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PaginationRequest(0, 10, "email", "asc")))
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].email").value("test@example.com"));
    }
} 