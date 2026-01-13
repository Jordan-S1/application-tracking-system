package com.ats.api.dto.response;

import com.ats.domain.entity.ApplicationNote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationNoteResponse {
    private Long id;
    private String content;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ApplicationNoteResponse fromEntity(ApplicationNote note) {
        return ApplicationNoteResponse.builder()
                .id(note.getId())
                .content(note.getContent())
                .createdByName(note.getCreatedBy().getFullName())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}
