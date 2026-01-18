package com.github.adaken4.lets_play.service;

import org.springframework.stereotype.Service;

import com.github.adaken4.lets_play.dto.UserResponse;
import com.github.adaken4.lets_play.model.User;

@Service
public class UserService {

    

    /**
     * Security: never expose password field in responses
     */
    private UserResponse mapToResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
