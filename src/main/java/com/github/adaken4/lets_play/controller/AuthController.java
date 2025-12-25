package com.github.adaken4.lets_play.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.adaken4.lets_play.dto.JwtResponse;
import com.github.adaken4.lets_play.dto.LoginRequest;
import com.github.adaken4.lets_play.dto.MessageResponse;
import com.github.adaken4.lets_play.dto.SignupRequest;
import com.github.adaken4.lets_play.model.User;
import com.github.adaken4.lets_play.repository.UserRepository;
import com.github.adaken4.lets_play.security.JwtUtils;

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
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private JwtUtils jwtUtils;

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

    /**
     * Authenticates user credentials and returns JWT token response.
     * 
     * @param loginRequest validated login DTO (email, password)
     * @return JwtResponse with token, user details, and role
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Authenticate via Spring Security (calls CustomUserDetailsService +
        // PasswordEncoder)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()));

        // Store authentication in SecurityContext (for current request)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token using email as subject
        String jwt = jwtUtils.generateTokenFromUsername(loginRequest.getEmail());

        // Build JWT response with user details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername()).get();

        return ResponseEntity.ok(new JwtResponse(
                jwt, // JWT token string
                "Bearer", // Token type prefix
                user.getId(), // User ID for frontend
                user.getEmail(), // Email/username
                user.getRole() // Role for UI permissions
        ));
    }
}
