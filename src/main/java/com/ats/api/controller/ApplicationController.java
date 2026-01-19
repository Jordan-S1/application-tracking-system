package com.ats.api.controller;

import com.ats.api.dto.request.ApplicationRequest;
import com.ats.api.dto.request.ApplicationStatusUpdateRequest;
import com.ats.api.dto.response.ApplicationDetailResponse;
import com.ats.api.dto.response.ApplicationResponse;
import com.ats.domain.entity.ApplicationStatus;
import com.ats.domain.entity.User;
import com.ats.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Application Controller - Manages job applications.
 * Endpoints:
 * - POST /applications: Create a new application (CANDIDATE only)
 * - GET /applications/{id}: Get application details (CANDIDATE or RECRUITER)
 * - GET /applications: List user applications (CANDIDATE or RECRUITER)
 * - GET /applications/search: Search applications (CANDIDATE or RECRUITER)
 * - PUT /applications/{id}: Update application details (CANDIDATE only)
 * - PATCH /applications/{id}/status: Update application status (CANDIDATE only)
 * - DELETE /applications/{id}: Delete application (CANDIDATE only)
 * All endpoints require valid JWT in Authorization header.
 */
@Slf4j
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Job application management endpoints")
public class ApplicationController {
        private final ApplicationService applicationService;

        /**
         * Create a new job application.
         * Request Body:
         * {
         * "companyName": "Tech Corp",
         * "jobTitle": "Software Engineer",
         * "dateApplied": "2026-01-01",
         * "notes": "Applied via company website"
         * }
         * 
         * @param currentUser authenticated user (CANDIDATE)
         * @param request     application details
         * @return 201 Created with ApplicationResponse
         * @throws IllegalArgumentException if validation fails
         */
        @PostMapping
        @PreAuthorize("hasRole('CANDIDATE')")
        @Operation(summary = "Create a new application", description = "Candidate creates a new job application")
        public ResponseEntity<ApplicationResponse> createApplication(
                        @AuthenticationPrincipal User currentUser,
                        @Valid @RequestBody ApplicationRequest request) {
                ApplicationResponse response = applicationService.createApplication(currentUser, request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get application details", description = "Retrieve a specific application with full details")
        public ResponseEntity<ApplicationDetailResponse> getApplicationDetails(
                        @PathVariable Long id,
                        @AuthenticationPrincipal User currentUser) {
                ApplicationDetailResponse response = applicationService.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

                applicationService.validateOwnership(id, currentUser);
                return ResponseEntity.ok(response);
        }

        @GetMapping
        @Operation(summary = "List user applications", description = "Get paginated list of user's applications")
        public ResponseEntity<Page<ApplicationResponse>> listApplications(
                        @AuthenticationPrincipal User currentUser,
                        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
                        @Parameter(description = "Sort field") @RequestParam(defaultValue = "dateApplied") String sortBy,
                        @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") Sort.Direction direction) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
                Page<ApplicationResponse> response = applicationService.findByOwner(currentUser, pageable);
                return ResponseEntity.ok(response);
        }

        @GetMapping("/search")
        @Operation(summary = "Search applications", description = "Search applications by status and/or company name")
        public ResponseEntity<Page<ApplicationResponse>> searchApplications(
                        @AuthenticationPrincipal User currentUser,
                        @Parameter(description = "Application status filter") @RequestParam(required = false) ApplicationStatus status,
                        @Parameter(description = "Company name filter") @RequestParam(required = false) String companyName,
                        @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateApplied"));
                Page<ApplicationResponse> response = applicationService.searchApplications(
                                currentUser,
                                status,
                                companyName,
                                pageable);
                return ResponseEntity.ok(response);
        }

        /**
         * Update application details
         * Request Body:
         * {
         * "companyName": "Tech Corp",
         * "jobTitle": "Senior Software Engineer",
         * "dateApplied": "2026-01-01",
         * "notes": "Updated notes"
         * }
         * 
         * @param id          application ID
         * @param currentUser authenticated user (CANDIDATE)
         * @param request     updated application details
         * @return 200 OK with updated ApplicationResponse
         * @throws IllegalArgumentException if application not found or validation fails
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('CANDIDATE')")
        @Operation(summary = "Update application details", description = "Update application details (company, title, etc.)")
        public ResponseEntity<ApplicationResponse> updateApplication(
                        @PathVariable Long id,
                        @AuthenticationPrincipal User currentUser,
                        @Valid @RequestBody ApplicationRequest request) {
                applicationService.validateOwnership(id, currentUser);
                ApplicationResponse response = applicationService.updateApplication(id, request);
                return ResponseEntity.ok(response);
        }

        /**
         * Update application status with audit trail.
         * Request Body:
         * {
         * "newStatus": "INTERVIEW_SCHEDULED",
         * "comments": "Interview scheduled for next week"
         * }
         */
        @PatchMapping("/{id}/status")
        @Operation(summary = "Update application status", description = "Transition application to next status with audit trail")
        public ResponseEntity<ApplicationResponse> updateApplicationStatus(
                        @PathVariable Long id,
                        @AuthenticationPrincipal User currentUser,
                        @Valid @RequestBody ApplicationStatusUpdateRequest request) {
                applicationService.validateOwnership(id, currentUser);
                ApplicationResponse response = applicationService.updateApplicationStatus(id, request, currentUser);
                return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('CANDIDATE')")
        @Operation(summary = "Delete application", description = "Remove an application")
        public ResponseEntity<Void> deleteApplication(
                        @PathVariable Long id,
                        @AuthenticationPrincipal User currentUser) {
                applicationService.validateOwnership(id, currentUser);
                applicationService.deleteApplication(id);
                return ResponseEntity.noContent().build();
        }
}