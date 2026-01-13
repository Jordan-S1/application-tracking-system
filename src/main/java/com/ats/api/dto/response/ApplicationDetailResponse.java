package com.ats.api.dto.response;

import com.ats.domain.entity.Application;
import com.ats.domain.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDetailResponse {
    private Long id;
    private String companyName;
    private String jobTitle;
    private LocalDate dateApplied;
    private ApplicationStatus status;
    private String jobUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ApplicationStatusHistoryResponse> statusHistory;
    private List<ApplicationNoteResponse> applicationNotes;

    public static ApplicationDetailResponse fromEntity(Application application) {
        return ApplicationDetailResponse.builder()
                .id(application.getId())
                .companyName(application.getCompanyName())
                .jobTitle(application.getJobTitle())
                .dateApplied(application.getDateApplied())
                .status(application.getStatus())
                .jobUrl(application.getJobUrl())
                .notes(application.getNotes())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .statusHistory(application.getStatusHistory().stream()
                        .map(ApplicationStatusHistoryResponse::fromEntity)
                        .toList())
                .applicationNotes(application.getApplicationNotes().stream()
                        .map(ApplicationNoteResponse::fromEntity)
                        .toList())
                .build();
    }
}