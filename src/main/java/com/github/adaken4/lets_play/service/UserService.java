package com.github.adaken4.lets_play.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.adaken4.lets_play.dto.UserCreationRequest;
import com.github.adaken4.lets_play.dto.UserResponse;
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
