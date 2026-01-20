package com.github.adaken4.lets_play.dto;

import jakarta.validation.constraints.Positive;

public record ProductUpdateRequest(
        String name,
        String description,
        @Positive Double price) {
}
