package com.ats.domain.repository;

import com.ats.domain.entity.User;
import com.ats.domain.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
