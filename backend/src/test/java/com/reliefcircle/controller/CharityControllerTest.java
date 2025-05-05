package com.reliefcircle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.dto.PaginationRequest;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CharityControllerTest {

    @Mock
    private CharityService charityService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private UserDetails userDetails;
    private CharityDto testCharityDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        CharityController controller = new CharityController(charityService, userRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .role(User.UserRole.FUNDRAISER)
                .build();

        userDetails = new org.springframework.security.core.userdetails.User(
                testUser.getEmail(),
                "password",
                Arrays.asList(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_FUNDRAISER"))
        );

        testCharityDto = CharityDto.builder()
                .name("Test Charity")
                .description("Test Description")
                .organizationName("Test Org")
                .category("Education")
                .targetAmount(new BigDecimal("1000.00"))
                .fundraiserId(testUser.getId())
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
    }

    @Test
    void testGetAllCharities() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<CharityDto> page = new PageImpl<>(Arrays.asList(testCharityDto));
        when(charityService.getAllCharities(any(PageRequest.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/charities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PaginationRequest(0, 10, "name", "asc")))
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].name").value("Test Charity"));
    }

    @Test
    void testGetCharityById() throws Exception {
        // Arrange
        when(charityService.getCharityById(anyLong())).thenReturn(testCharityDto);

        // Act & Assert
        mockMvc.perform(get("/api/charities/1")
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Charity"));
    }

    @Test
    void testCreateCharity() throws Exception {
        // Arrange
        when(charityService.registerCharity(any(CharityDto.class))).thenReturn(testCharityDto);
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/charities")
                .file(file)
                .param("name", "Test Charity")
                .param("description", "Test Description")
                .param("organizationName", "Test Org")
                .param("category", "Education")
                .param("targetAmount", "1000.00")
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Charity"));

        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    void testUpdateCharity() throws Exception {
        // Arrange
        when(charityService.updateCharity(anyLong(), any(CharityDto.class))).thenReturn(testCharityDto);
        when(charityService.getCharityById(anyLong())).thenReturn(testCharityDto);
        when(authentication.getPrincipal()).thenReturn(testUser); // Use User object for update endpoint

        // Act & Assert
        mockMvc.perform(put("/api/charities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCharityDto))
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Test Charity"));

        // Reset authentication principal for other tests
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    @Test
    void testVerifyCharity() throws Exception {
        // Arrange
        when(charityService.verifyCharity(anyLong())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(put("/api/charities/1/verify")
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().string("Charity verified successfully"));
    }

    @Test
    void testGetVerifiedCharities() throws Exception {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<CharityDto> page = new PageImpl<>(Arrays.asList(testCharityDto));
        when(charityService.getVerifiedCharities(any(PageRequest.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/charities/verified")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PaginationRequest(0, 10, "name", "asc")))
                .with(request -> {
                    request.setUserPrincipal(authentication);
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].name").value("Test Charity"));
    }
} 