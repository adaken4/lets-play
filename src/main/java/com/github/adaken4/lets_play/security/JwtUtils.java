package com.github.adaken4.lets_play.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;

/**
 * JWT utility class for generating, parsing, and validating JSON Web Tokens.
 * Handles token creation with user email as subject and 24-hour expiration.
 * Reads secret key from application properties for security.
 */
@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMs}")
    private int jwtExpirationMs;

    /**
     * Creates HMAC signing key from Base64-decoded secret.
     * 
     * @return SecretKey for JWT signing/verification
     */
    private SecretKey key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

}
