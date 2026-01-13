package com.ats.api.dto.response;

import com.ats.domain.entity.ApplicationStatusHistory;
import com.ats.domain.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistoryResponse {
    private Long id;
    private ApplicationStatus oldStatus;
    private ApplicationStatus newStatus;
    private String createdByName;
    private String reason;
    private LocalDateTime createdAt;

    public static ApplicationStatusHistoryResponse fromEntity(ApplicationStatusHistory history) {
        return ApplicationStatusHistoryResponse.builder()
                .id(history.getId())
                .oldStatus(history.getOldStatus())
                .newStatus(history.getNewStatus())
                .createdByName(history.getCreatedBy().getFullName())
                .reason(history.getReason())
                .createdAt(history.getCreatedAt())
                .build();
    }
}