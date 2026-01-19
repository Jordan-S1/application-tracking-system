package com.ats.service.impl;

import com.ats.api.dto.request.ApplicationRequest;
import com.ats.api.dto.request.ApplicationStatusUpdateRequest;
import com.ats.api.dto.response.ApplicationResponse;
import com.ats.domain.entity.Application;
import com.ats.domain.entity.ApplicationStatus;
import com.ats.domain.entity.User;
import com.ats.domain.entity.UserRole;
import com.ats.domain.repository.ApplicationRepository;
import com.ats.domain.repository.ApplicationStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Unit tests for ApplicationServiceImpl */
@ExtendWith(MockitoExtension.class)
public class ApplicationServiceImplTest {
    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private ApplicationStatusHistoryRepository statusHistoryRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private User testUser;
    private ApplicationRequest applicationRequest;
    private Application testApplication;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("candidate")
                .email("candidate@example.com")
                .password("encoded_password")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.CANDIDATE)
                .enabled(true)
                .build();

        applicationRequest = ApplicationRequest.builder()
                .companyName("Google")
                .jobTitle("Software Engineer")
                .dateApplied(LocalDate.now())
                .jobUrl("https://google.com/jobs/123")
                .notes("Great opportunity")
                .build();

        testApplication = Application.builder()
                .id(1L)
                .owner(testUser)
                .companyName("Google")
                .jobTitle("Software Engineer")
                .dateApplied(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .jobUrl("https://google.com/jobs/123")
                .notes("Great opportunity")
                .build();
    }

    @Test
    void testCreateApplicationSuccess() {
        // Arrange
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);

        // Act
        ApplicationResponse response = applicationService.createApplication(testUser, applicationRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Google", response.getCompanyName());
        assertEquals("Software Engineer", response.getJobTitle());
        assertEquals(ApplicationStatus.APPLIED, response.getStatus());
        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    void testFindByIdSuccess() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // Act
        var result = applicationService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Google", result.get().getCompanyName());
    }

    @Test
    void testFindByIdNotFound() {
        // Arrange
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        var result = applicationService.findById(999L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateApplicationSuccess() {
        // Arrange
        ApplicationRequest updateRequest = ApplicationRequest.builder()
                .companyName("Microsoft")
                .jobTitle("Senior Developer")
                .dateApplied(LocalDate.now())
                .build();

        Application updatedApplication = Application.builder()
                .id(1L)
                .owner(testUser)
                .companyName("Microsoft")
                .jobTitle("Senior Developer")
                .dateApplied(LocalDate.now())
                .status(ApplicationStatus.APPLIED)
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(updatedApplication);

        // Act
        ApplicationResponse response = applicationService.updateApplication(1L, updateRequest);

        // Assert
        assertNotNull(response);
        assertEquals("Microsoft", response.getCompanyName());
        assertEquals("Senior Developer", response.getJobTitle());
    }

    @Test
    void testUpdateApplicationNotFound() {
        // Arrange
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> applicationService.updateApplication(999L, applicationRequest));
    }

    @Test
    void testDeleteApplicationSuccess() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // Act
        applicationService.deleteApplication(1L);

        // Assert
        verify(applicationRepository, times(1)).delete(testApplication);
    }

    @Test
    void testDeleteApplicationNotFound() {
        // Arrange
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> applicationService.deleteApplication(999L));
    }

    @Test
    void testUpdateApplicationStatusSuccess() {
        // Arrange
        ApplicationStatusUpdateRequest statusRequest = ApplicationStatusUpdateRequest.builder()
                .newStatus(ApplicationStatus.PHONE_SCREEN)
                .reason("Passed initial review")
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));
        when(applicationRepository.save(any(Application.class))).thenReturn(testApplication);
        when(statusHistoryRepository.save(any())).thenReturn(null);

        // Act
        ApplicationResponse response = applicationService.updateApplicationStatus(1L, statusRequest, testUser);

        // Assert
        assertNotNull(response);
        verify(statusHistoryRepository, times(1)).save(any());
    }

    @Test
    void testUpdateApplicationStatusInvalidTransition() {
        // Arrange
        testApplication.setStatus(ApplicationStatus.REJECTED);
        ApplicationStatusUpdateRequest statusRequest = ApplicationStatusUpdateRequest.builder()
                .newStatus(ApplicationStatus.PHONE_SCREEN)
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> applicationService.updateApplicationStatus(1L, statusRequest, testUser));
    }

    @Test
    void testValidateOwnershipSuccess() {
        // Arrange
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> applicationService.validateOwnership(1L, testUser));
    }

    @Test
    void testValidateOwnershipFail() {
        // Arrange
        User otherUser = User.builder()
                .id(2L)
                .username("other")
                .email("other@example.com")
                .role(UserRole.CANDIDATE)
                .build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(testApplication));

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> applicationService.validateOwnership(1L, otherUser));
    }
}