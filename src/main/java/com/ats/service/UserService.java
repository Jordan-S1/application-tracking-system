package com.ats.service;

import com.ats.api.dto.request.UserRegistrationRequest;
import com.ats.api.dto.response.UserResponse;
import com.ats.domain.entity.User;

import java.util.Optional;

/**
 * Service interface for managing Users.
 * Defines methods for user registration and retrieval.
 */
public interface UserService {

    UserResponse register(UserRegistrationRequest request);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<UserResponse> findById(Long id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}