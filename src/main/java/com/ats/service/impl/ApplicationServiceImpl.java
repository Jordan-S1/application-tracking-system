package com.ats.service.impl;

import com.ats.api.dto.request.ApplicationRequest;
import com.ats.api.dto.request.ApplicationStatusUpdateRequest;
import com.ats.api.dto.response.ApplicationDetailResponse;
import com.ats.api.dto.response.ApplicationResponse;
import com.ats.domain.entity.Application;
import com.ats.domain.entity.ApplicationStatus;
import com.ats.domain.entity.ApplicationStatusHistory;
import com.ats.domain.entity.User;
import com.ats.domain.repository.ApplicationRepository;
import com.ats.domain.repository.ApplicationStatusHistoryRepository;
import com.ats.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for managing Applications.
 * Provides methods for creating, retrieving, updating, deleting,
 * and searching applications, as well as updating application status
 * and validating ownership.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

        private final ApplicationRepository applicationRepository;
        private final ApplicationStatusHistoryRepository statusHistoryRepository;

        @Override
        public ApplicationResponse createApplication(User owner, ApplicationRequest request) {
                Application application = Application.builder()
                                .owner(owner)
                                .companyName(request.getCompanyName())
                                .jobTitle(request.getJobTitle())
                                .dateApplied(request.getDateApplied())
                                .jobUrl(request.getJobUrl())
                                .notes(request.getNotes())
                                .status(ApplicationStatus.APPLIED)
                                .build();

                Application saved = applicationRepository.save(application);
                log.info("Application created: {} at {}", saved.getId(), saved.getCompanyName());
                return ApplicationResponse.fromEntity(saved);
        }

        @Override
        @Transactional(readOnly = true)
        public Optional<ApplicationDetailResponse> findById(Long id) {
                return applicationRepository.findById(id)
                                .map(ApplicationDetailResponse::fromEntity);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<ApplicationResponse> findByOwner(User owner, Pageable pageable) {
                return applicationRepository.findByOwner(owner, pageable)
                                .map(ApplicationResponse::fromEntity);
        }

        @Override
        @Transactional(readOnly = true)
        public Page<ApplicationResponse> searchApplications(
                        User owner,
                        ApplicationStatus status,
                        String companyName,
                        Pageable pageable) {
                return applicationRepository.searchApplications(owner, status, companyName, pageable)
                                .map(ApplicationResponse::fromEntity);
        }

        @Override
        public ApplicationResponse updateApplication(Long id, ApplicationRequest request) {
                Application application = applicationRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

                application.setCompanyName(request.getCompanyName());
                application.setJobTitle(request.getJobTitle());
                application.setDateApplied(request.getDateApplied());
                application.setJobUrl(request.getJobUrl());
                application.setNotes(request.getNotes());

                Application updated = applicationRepository.save(application);
                log.info("Application updated: {}", id);
                return ApplicationResponse.fromEntity(updated);
        }

        @Override
        public void deleteApplication(Long id) {
                Application application = applicationRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

                applicationRepository.delete(application);
                log.info("Application deleted: {}", id);
        }

        @Override
        public ApplicationResponse updateApplicationStatus(
                        Long id,
                        ApplicationStatusUpdateRequest request,
                        User updatedBy) {
                Application application = applicationRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + id));

                ApplicationStatus oldStatus = application.getStatus();
                ApplicationStatus newStatus = request.getNewStatus();

                if (!oldStatus.canTransitionTo(newStatus)) {
                        throw new IllegalStateException(
                                        String.format("Cannot transition from %s to %s", oldStatus, newStatus));
                }

                application.updateStatus(newStatus);
                applicationRepository.save(application);

                // Record in audit trail
                ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                                .application(application)
                                .oldStatus(oldStatus)
                                .newStatus(newStatus)
                                .createdBy(updatedBy)
                                .reason(request.getReason())
                                .build();

                statusHistoryRepository.save(history);
                log.info("Application {} status updated from {} to {}", id, oldStatus, newStatus);

                return ApplicationResponse.fromEntity(application);
        }

        @Override
        @Transactional(readOnly = true)
        public void validateOwnership(Long applicationId, User user) {
                Application application = applicationRepository.findById(applicationId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Application not found: " + applicationId));

                if (!application.getOwner().getId().equals(user.getId())) {
                        throw new IllegalArgumentException("User is not the owner of this application");
                }
        }
}