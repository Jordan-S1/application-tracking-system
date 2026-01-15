package com.ats.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for application note requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationNoteRequest {
    @NotBlank(message = "Note content is required")
    private String content;
}