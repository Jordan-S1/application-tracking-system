package com.ats.domain.entity;

/**
 * Enum representing user roles within the application tracking system.
 */
public enum UserRole {
    CANDIDATE,
    RECRUITER;

    public boolean isCandidate() {
        return this == CANDIDATE;
    }

    public boolean isRecruiter() {
        return this == RECRUITER;
    }

}
