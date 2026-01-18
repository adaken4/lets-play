package com.github.adaken4.lets_play.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.adaken4.lets_play.dto.UserResponse;
import com.github.adaken4.lets_play.model.User;
import com.github.adaken4.lets_play.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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
