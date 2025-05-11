package com.reliefcircle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.reliefcircle.dto.UserDto;
import com.reliefcircle.exception.ResourceNotFoundException;
import com.reliefcircle.model.User;
import com.reliefcircle.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserById(UUID UserId) {
        return userRepository.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User profile not found with ID: %s", UserId)));
    }

    public User updateVolunteerStatus(UUID userId, boolean isVolunteer) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.getRole().equals(User.UserRole.DONOR)) {
            throw new IllegalStateException("Only donors can become volunteers");
        }

        user.setIsVolunteer(isVolunteer);
        User savedUser = userRepository.save(user);
        
        log.info("Updated volunteer status for user {}: {}", userId, isVolunteer);
        
        return savedUser;
    }
    
    
}