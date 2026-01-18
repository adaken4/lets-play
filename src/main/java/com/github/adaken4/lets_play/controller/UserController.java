package com.github.adaken4.lets_play.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestBody;

import com.github.adaken4.lets_play.dto.UserCreationRequest;
import com.github.adaken4.lets_play.dto.UserResponse;
import com.github.adaken4.lets_play.service.UserService;
import com.github.adaken4.lets_play.service.CustomUserDetailsService.UserDetailsImpl;

import jakarta.validation.Valid;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * POST /api/users - Admin creates a new user with a specific role.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreationRequest request, Authentication auth) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.created(location(response.id())).body(response);
    }

    /**
     * GET /api/users/me
     * Allows any authenticated user to view THEIR OWN profile safely.
     * @param auth Authentication object containing user details (principal).
     * @return UserResponse with user's own details.
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Authentication auth) {
        // Extract the custom user details (principal) from the authentication object
        String userId = ((UserDetailsImpl) auth.getPrincipal()).getId();
        return ResponseEntity.ok(userService.findById(userId));
    }

    private URI location(String id) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(id).toUri();
    }

}
