package com.reliefcircle.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate() {
        return ResponseEntity.ok("Logged out successfully. Please log in to continue.");
    }
}