package com.ats.domain.entity;

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