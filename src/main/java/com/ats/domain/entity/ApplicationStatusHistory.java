package com.ats.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Application Status History entity representing the history of status changes
 * for job applications.
 * Includes references to the application and the user who made the change.
 * Indexes are created on application_id, created_by, and created_at for
 * optimized queries.
 */
@Entity
@Table(name = "application_status_history", indexes = {
        @Index(name = "idx_application_id", columnList = "application_id"),
        @Index(name = "idx_created_by", columnList = "created_by"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}