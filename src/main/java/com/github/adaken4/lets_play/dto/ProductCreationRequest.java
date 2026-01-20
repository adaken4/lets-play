package com.github.adaken4.lets_play.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProductCreationRequest(
        @NotBlank String name,
        @NotBlank String description,
        @Positive Double price) {
}
