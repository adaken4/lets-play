package com.github.adaken4.lets_play.config;

import java.time.Duration;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.github.adaken4.lets_play.dto.ApiErrorResponse;

import io.github.bucket4j.Bucket;

import java.util.concurrent.ConcurrentHashMap;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * Token-bucket rate limiter for API endpoints.
 * - Auth endpoints: 5 req/min per IP
 * - Other API endpoints: 100 req/min per IP
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // JSON serializer for error responses
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Thread-safe storage for per-IP rate limit buckets
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> globalBuckets = new ConcurrentHashMap<>();

    /**
     * Creates rate limit bucket for authentication endpoints.
     * @return Bucket with 5 requests per minute limit
     */
    private Bucket createAuthBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(5)
                        .refillIntervally(5, Duration.ofMinutes(1)))
                .build();
    }

    /**
     * Creates rate limit bucket for general API endpoints.
     * @return Bucket with 100 requests per minute limit
     */
    private Bucket createGlobalBucket() {
        return Bucket.builder()
                .addLimit(limit -> limit
                        .capacity(100)
                        .refillIntervally(100, Duration.ofMinutes(1)))
                .build();
    }

    /**
     * Attempts to consume one token from the bucket.
     * Returns false and writes 429 error if bucket empty.
     */
    private boolean consumeToken(Bucket bucket, HttpServletResponse response) throws Exception {

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");

            ApiErrorResponse error = new ApiErrorResponse(
                    429,
                    "Too Many Requests",
                    "Rate limit exceeded. Please try again in a minute.",
                    System.currentTimeMillis());

            response.getWriter().write(objectMapper.writeValueAsString(error));
            return false;
        }
    }
    
}
