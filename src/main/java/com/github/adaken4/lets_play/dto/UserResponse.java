package com.github.adaken4.lets_play.dto;

public record UserResponse(
        String id,
        String name,
        String email,
        String role) {
}
