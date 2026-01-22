package com.github.adaken4.lets_play.dto;

public record ApiErrorResponse(
    int status,
    String error,
    String message,
    long timestamp
) {
}
