package com.github.adaken4.lets_play.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;

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

    /**
     * Generates JWT token for user authentication.
     * 
     * @param username user's email (stored as token subject)
     * @return compact JWT string
     */
    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    /**
     * Extracts username (email) from valid JWT token.
     * 
     * @param token JWT token string
     * @return username from token subject
     * @throws io.jsonwebtoken.JwtException if token invalid/malformed
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().verifyWith(key()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

}
