package com.reliefcircle.service;

import com.reliefcircle.exception.ResourceNotFoundException;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;
import com.reliefcircle.util.aws.FileStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStore fileStore;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        // Arrange
        PageRequest pageRequest = PageRequest.of(0, 10);
        User user1 = User.builder().id(UUID.randomUUID()).email("user1@example.com").build();
        User user2 = User.builder().id(UUID.randomUUID()).email("user2@example.com").build();
        Page<User> expectedPage = new PageImpl<>(Arrays.asList(user1, user2));

        when(userRepository.findAll(pageRequest)).thenReturn(expectedPage);

        // Act
        Page<User> result = userService.getAllUsers(pageRequest);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(userRepository).findAll(pageRequest);
    }

    @Test
    void testGetUserById_Success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User expectedUser = User.builder().id(userId).email("test@example.com").build();

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(expectedUser));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("test@example.com", result.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_NotFound() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void testUploadUserImage() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("test@example.com").build();
        MultipartFile file = mock(MultipartFile.class);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(1024L);

        // Act
        userService.uploadUserImage(userId, file);

        // Assert
        verify(fileStore).save(anyString(), anyString(), any(), any());
        verify(userRepository).save(any(User.class));
    }
} 