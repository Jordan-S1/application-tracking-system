package com.ats.domain.entity;

/**
 * Application status enum defining the valid states in the job application lifecycle.
 * Workflow: APPLIED -> PHONE_SCREEN -> INTERVIEW -> OFFER -> ACCEPTED/REJECTED.
 * ACCEPTED and REJECTED are terminal states - no further transitions allowed.
 * The canTransitionTo() method enforces valid state transitions.
 */
public enum ApplicationStatus {
    APPLIED,
    PHONE_SCREEN,
    INTERVIEW,
    OFFER,
    ACCEPTED,
    REJECTED;

    public boolean canTransitionTo(ApplicationStatus nextStatus) {
        return switch (this) {
            case APPLIED -> nextStatus == PHONE_SCREEN || nextStatus == REJECTED;
            case PHONE_SCREEN -> nextStatus == INTERVIEW || nextStatus == REJECTED;
            case INTERVIEW -> nextStatus == OFFER || nextStatus == REJECTED;
            case OFFER -> nextStatus == ACCEPTED || nextStatus == REJECTED;
            case ACCEPTED, REJECTED -> false;  // Terminal states
        };
    }

    public static boolean isTerminalState(ApplicationStatus status) {
        return status == ACCEPTED || status == REJECTED;
    }
}