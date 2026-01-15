package com.ats.api.dto.request;

import com.ats.domain.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for application status update requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusUpdateRequest {
    @NotNull(message = "New status is required")
    private ApplicationStatus newStatus;

    private String reason;
}