package com.ats.service;

import com.ats.api.dto.request.ApplicationRequest;
import com.ats.api.dto.request.ApplicationStatusUpdateRequest;
import com.ats.api.dto.response.ApplicationDetailResponse;
import com.ats.api.dto.response.ApplicationResponse;
import com.ats.domain.entity.ApplicationStatus;
import com.ats.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing Applications.
 * Defines methods for creating, retrieving, updating, deleting,
 * and searching applications, as well as updating application status
 * and validating ownership.
 */
public interface ApplicationService {

        ApplicationResponse createApplication(User owner, ApplicationRequest request);

        Optional<ApplicationDetailResponse> findById(Long id);

        Page<ApplicationResponse> findByOwner(User owner, Pageable pageable);

        Page<ApplicationResponse> searchApplications(
                        User owner,
                        ApplicationStatus status,
                        String companyName,
                        Pageable pageable);

        ApplicationResponse updateApplication(Long id, ApplicationRequest request);

        void deleteApplication(Long id);

        ApplicationResponse updateApplicationStatus(
                        Long id,
                        ApplicationStatusUpdateRequest request,
                        User updatedBy);

        void validateOwnership(Long applicationId, User user);
}