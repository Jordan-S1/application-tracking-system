package com.ats.domain.entity;

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
