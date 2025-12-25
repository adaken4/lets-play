package com.github.adaken4.lets_play.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adaken4.lets_play.dto.MessageResponse;
import com.github.adaken4.lets_play.dto.SignupRequest;
import com.github.adaken4.lets_play.model.User;
import com.github.adaken4.lets_play.repository.UserRepository;

import jakarta.validation.Valid;

/**
 * Authentication controller for user registration and JWT login.
 * Handles /api/auth/register and /api/auth/login endpoints.
 * Integrates Spring Security AuthenticationManager with JWT token generation.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;

    /**
     * Registers new user with email uniqueness check and BCrypt password hashing.
     * 
     * @param signUpRequest validated signup DTO (username, email, password)
     * @return success message or bad request if email exists
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Check email uniqueness
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create user entity with generated UUID and hashed password
        User user = new User(
                UUID.randomUUID().toString(),
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                "USER");

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
