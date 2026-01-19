package com.ats.api.controller;

import com.ats.api.dto.request.UserLoginRequest;
import com.ats.api.dto.request.UserRegistrationRequest;
import com.ats.api.dto.response.AuthResponse;
import com.ats.api.dto.response.UserResponse;
import com.ats.security.JwtTokenProvider;
import com.ats.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller - Handles user registration and login.
 * Endpoints:
 * - POST /api/auth/register: Register a new user (Candidate or Recruiter)
 * - POST /api/auth/login: Login and receive JWT token
 * These endpoints are publicly accessible (no JWT required).
 * Other endpoints require valid JWT in Authorization header.
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration and login endpoints")
public class AuthController {
        private final UserService userService;
        private final AuthenticationManager authenticationManager;
        private final JwtTokenProvider jwtTokenProvider;

        /**
         * Registers a new user account.
         * Request Body:
         * {
         * "username": "john_doe",
         * "email": "john@example.com",
         * "password": "Password123!",
         * "firstName": "John",
         * "lastName": "Doe",
         * "role": "CANDIDATE" // or "RECRUITER"
         * }
         * Response (201 Created):
         * {
         * "token": "eyJhbGc...",
         * "tokenType": "Bearer",
         * "user": { ... }
         * }
         *
         * @param request user registration details
         * @return 201 Created with AuthResponse containing token + user info
         * @throws IllegalArgumentException if email/username already exists
         */
        @PostMapping("/register")
        @Operation(summary = "Register a new user", description = "Create a new user account (Candidate or Recruiter)")
        public ResponseEntity<AuthResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
                log.info("Registration attempt for username: {}", request.getUsername());

                // Register user (validates email/username uniqueness, hashes password)
                UserResponse user = userService.register(request);

                // Generate JWT token immediately after registration (for convenience)
                String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());

                // Build response with token + user info
                AuthResponse response = AuthResponse.builder()
                                .token(token)
                                .user(user)
                                .build();

                log.info("User registered successfully: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        /**
         * Authenticates user and returns JWT token.
         * Request Body:
         * {
         * "usernameOrEmail": "john_doe", // Can be username or email
         * "password": "Password123!"
         * }
         * Response (200 OK):
         * {
         * "token": "eyJhbGc...",
         * "tokenType": "Bearer",
         * "user": {
         * "id": 1,
         * "username": "john_doe",
         * "email": "john@example.com",
         * "role": "CANDIDATE",
         * ...
         * }
         * }
         *
         * @param request username/email and password
         * @return 200 OK with AuthResponse containing token + user info
         * @throws BadCredentialsException (401) if credentials are invalid
         */
        @PostMapping("/login")
        @Operation(summary = "Login user", description = "Authenticate user and return JWT token")
        public ResponseEntity<AuthResponse> login(@Valid @RequestBody UserLoginRequest request) {
                log.info("Login attempt for: {}", request.getUsernameOrEmail());

                // Authenticate using Spring Security's AuthenticationManager
                // This will:
                // 1. Load user from database via CustomUserDetailsService
                // 2. Compare provided password with stored hash using Bcrypt
                // 3. Return Authentication object if valid, throw exception if invalid
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                                request.getUsernameOrEmail(),
                                                request.getPassword()));

                // Generate JWT token from authenticated user
                String token = jwtTokenProvider.generateToken(authentication);

                // Fetch user details for response
                UserResponse user = userService.findByUsername(authentication.getName())
                                .map(UserResponse::fromEntity)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                // Build response
                AuthResponse response = AuthResponse.builder()
                                .token(token)
                                .user(user)
                                .build();

                log.info("User logged in successfully: {}", user.getUsername());
                return ResponseEntity.ok(response);
        }
}