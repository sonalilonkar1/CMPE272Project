package com.reliefcircle.service;

import com.reliefcircle.dto.CharityDto;
import com.reliefcircle.exception.CharityException;
import com.reliefcircle.model.Charity;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.CharityRepository;
import com.reliefcircle.repository.DonationRepository;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.repository.VerificationRepository;
import com.reliefcircle.config.PayPalConfig;
import com.reliefcircle.service.AWSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CharityServiceTest {

    @Mock
    private CharityRepository charityRepository;

    @Mock
    private DonationRepository donationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private AWSService awsService;

    @Mock
    private PayPalConfig payPalConfig;

    @InjectMocks
    private CharityService charityService;

    private User testFundraiser;
    private CharityDto testCharityDto;
    private User testUser;
    private Charity testCharity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testFundraiser = User.builder()
                .id(UUID.randomUUID())
                .email("fundraiser@example.com")
                .role(User.UserRole.FUNDRAISER)
                .build();

        testCharityDto = CharityDto.builder()
                .name("Test Charity")
                .description("Test Description")
                .organizationName("Test Org")
                .category("Education")
                .targetAmount(new BigDecimal("1000.00"))
                .fundraiserId(testFundraiser.getId())
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .role(User.UserRole.FUNDRAISER)
                .build();

        testCharity = Charity.builder()
                .id(1L)
                .name("Test Charity")
                .description("Test Description")
                .organizationName("Test Org")
                .category("Education")
                .targetAmount(new BigDecimal("1000.00"))
                .fundraiser(testUser)
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testFundraiser));
    }

    @Test
    void testRegisterCharity_Success() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE, 
            "test image content".getBytes()
        );
        testCharityDto.setFile(file);
        
        when(charityRepository.save(any(Charity.class))).thenReturn(testCharity);
        when(awsService.uploadProofDocument(any(MultipartFile.class), anyString())).thenReturn("https://test-bucket.s3.amazonaws.com/test.jpg");

        // Act
        CharityDto result = charityService.registerCharity(testCharityDto);

        // Assert
        assertNotNull(result);
        assertEquals(testCharityDto.getName(), result.getName());
        verify(charityRepository).save(any(Charity.class));
    }

    @Test
    void testGetAllCharities() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Charity> page = new PageImpl<>(Arrays.asList(testCharity));
        when(charityRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // Act
        Page<CharityDto> result = charityService.getAllCharities(pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCharity.getName(), result.getContent().get(0).getName());
    }

    @Test
    void testGetVerifiedCharities() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name").ascending());
        Page<Charity> page = new PageImpl<>(Arrays.asList(testCharity));
        when(charityRepository.findByIsVerified(eq(true), any(PageRequest.class))).thenReturn(page);

        // Act
        Page<CharityDto> result = charityService.getVerifiedCharities(pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(testCharity.getName(), result.getContent().get(0).getName());
    }

    @Test
    void testGetCharityById() {
        // Arrange
        when(charityRepository.findById(anyLong())).thenReturn(Optional.of(testCharity));

        // Act
        CharityDto result = charityService.getCharityById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testCharity.getName(), result.getName());
    }

    @Test
    void testUpdateCharity() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg",
            MediaType.IMAGE_JPEG_VALUE, 
            "test image content".getBytes()
        );
        testCharityDto.setFile(file);
        
        when(charityRepository.findById(anyLong())).thenReturn(Optional.of(testCharity));
        when(charityRepository.save(any(Charity.class))).thenReturn(testCharity);
        when(awsService.uploadProofDocument(any(MultipartFile.class), anyString())).thenReturn("https://test-bucket.s3.amazonaws.com/test.jpg");

        // Act
        CharityDto result = charityService.updateCharity(1L, testCharityDto);

        // Assert
        assertNotNull(result);
        assertEquals(testCharityDto.getName(), result.getName());
        verify(charityRepository).save(any(Charity.class));
    }
} 