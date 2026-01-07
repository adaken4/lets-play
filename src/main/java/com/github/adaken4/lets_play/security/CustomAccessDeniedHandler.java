package com.github.adaken4.lets_play.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    // Jackson ObjectMapper for JSON serialization
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Set HTTP 403 Forbidden status
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        // Create minimal, clean error response body
        Map<String, String> body = new HashMap<>();
        body.put("error", "Forbidden");
        body.put("message", "You do not have permission to access this resource.");

        // Write JSON response directly to output stream
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}