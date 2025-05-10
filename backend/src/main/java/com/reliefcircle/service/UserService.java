package com.reliefcircle.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    private final UserRepository UserRepository;

    public Page<User> getAllUsers(Pageable pageable) {
        return UserRepository.findAll(pageable);
    }

    public User getUserById(UUID UserId) {
        return UserRepository.findById(UserId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("User profile not found with ID: %s", UserId)));
    }
    
    
}