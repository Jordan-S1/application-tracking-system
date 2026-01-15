package com.ats.api.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for application requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationRequest {
    @NotBlank(message = "Company name is required")
    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @NotBlank(message = "Job title is required")
    @Size(max = 255, message = "Job title must not exceed 255 characters")
    private String jobTitle;

    @NotNull(message = "Date applied is required")
    @PastOrPresent(message = "Date applied cannot be in the future")
    private LocalDate dateApplied;

    @Size(max = 1000, message = "Job URL must not exceed 1000 characters")
    private String jobUrl;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}