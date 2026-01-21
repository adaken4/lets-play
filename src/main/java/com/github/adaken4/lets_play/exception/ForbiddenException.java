package com.github.adaken4.lets_play.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super("Forbidden: " + message);
    }
}
