package com.github.adaken4.lets_play.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreationRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 40) String password,
        @NotBlank String role // System Admin can specify role (e.g., USER, ADMIN)
) {
}
