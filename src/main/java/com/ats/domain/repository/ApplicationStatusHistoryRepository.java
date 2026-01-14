package com.ats.domain.repository;

import com.ats.domain.entity.Application;
import com.ats.domain.entity.ApplicationStatusHistory;
import com.ats.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing ApplicationStatusHistory entities.
 */
@Repository
public interface ApplicationStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {

    List<ApplicationStatusHistory> findByApplicationOrderByCreatedAtDesc(Application application);

    List<ApplicationStatusHistory> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    List<ApplicationStatusHistory> findByApplicationAndCreatedAtBetweenOrderByCreatedAtDesc(
            Application application,
            LocalDateTime startTime,
            LocalDateTime endTime);
}