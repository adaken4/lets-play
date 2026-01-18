package com.github.adaken4.lets_play.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.adaken4.lets_play.dto.UserCreationRequest;
import com.github.adaken4.lets_play.dto.UserResponse;
import com.github.adaken4.lets_play.dto.UserUpdateRequest;
import com.github.adaken4.lets_play.model.User;
import com.github.adaken4.lets_play.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder encoder;

    /**
     * Creates a user after email uniqueness check.
     */
    public UserResponse createUser(UserCreationRequest request) {
        validateUniqueEmail(request.email());
        User user = new User(
            UUID.randomUUID().toString(),
            request.name(),
            request.email(),
            encoder.encode(request.password()), // Always hash before save
            request.role()
        );
        return mapToResponse(userRepository.save(user));
    }

    /**
     * Streams all users through mapper
     */
    public List<UserResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Blocking findById - throws on missing user (usecase contract)
     */
    public UserResponse findById(String id) {
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Partial update (PATCH) - only non-null fields are updated.
     * Always re-hash password if provided
     */
    public UserResponse patchUser(String userId, UserUpdateRequest updates) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Partial update pattern - null fields = no change
        if (updates.name() != null)
            existing.setName(updates.name());
        if (updates.email() != null)
            existing.setEmail(updates.email());
        if (updates.password() != null) {
            existing.setPassword(encoder.encode(updates.password()));
        }

        return mapToResponse(userRepository.save(existing));
    }

    /**
     * Single query email check prevents race conditions during concurrent registration
     */
    private void validateUniqueEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already in use");
        }
    }

    /**
     * Security: never expose password field in responses
     */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
