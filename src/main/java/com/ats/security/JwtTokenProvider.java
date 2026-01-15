package com.ats.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Token Provider - Generates and validates JWT tokens for authentication.
 * ------------
 * JWT (JSON Web Token) Workflow:
 * 1. User logs in with credentials
 * 2. Server generates signed JWT token
 * 3. Client stores token (localStorage, sessionStorage, etc.)
 * 4. Client includes token in Authorization header for each request: "Bearer <token>"
 * 5. Server validates token signature and expiration
 * 6. If valid, request is processed; if invalid, return 401 Unauthorized
 * ------------
 * Token Structure: header.payload.signature
 * - Header: Token type (JWT) and algorithm (HS512)
 * - Payload: Username (subject), issued at, expiration
 * - Signature: HMAC-SHA512 hash signed with secret key
 * ------------
 * Security Benefits:
 * - Stateless: No server-side session storage needed
 * - Scalable: Can be validated on any server with the secret key
 * - Immutable: Signature prevents tampering
 * - Expiration: Tokens expire automatically for security
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    /**
     TODO
    /** JWT secret key from configuration - change in production! */
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    /** JWT token expiration time in milliseconds (default: 24 hours) */
    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * Creates a SecretKey from the JWT secret string.
     * Extracted to reduce code duplication.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generates a JWT token from an authenticated user.
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        // Build and sign JWT token
        return Jwts.builder()
                .subject(username)                      // Username stored in token
                .issuedAt(now)                          // Token creation time
                .expiration(expiryDate)                 // Token expiration time
                .signWith(getSigningKey(), Jwts.SIG.HS512) // Sign with secret key
                .compact();                             // Serialize to compact form
    }

    /**
     * Generates a JWT token from a username string.
     * Useful when you already have username and don't need full Authentication object.
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     */
    public String getUsernameFromToken(String token) {
        // Parse token and validate signature
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())  // Verify signature with key
                .build()
                .parseSignedClaims(token)     // Parse the token
                .getPayload();                // Get payload claims

        // Extract username from "subject" claim
        return claims.getSubject();
    }

    /**
     * Validates a JWT token.
     * Checks:
     * 1. Signature is valid (not tampered)
     * 2. Token hasn't expired
     * 3. Token format is correct
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())  // Verify signature
                    .build()
                    .parseSignedClaims(token);    // Parse and validate

            return true;
        } catch (Exception ex) {
            log.error("Invalid JWT token: {}", ex.getMessage());
            return false;
        }
    }
}