package com.ats.domain.repository;

import com.ats.domain.entity.Application;
import com.ats.domain.entity.ApplicationStatus;
import com.ats.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    Page<Application> findByOwner(User owner, Pageable pageable);

    Page<Application> findByOwnerAndStatus(User owner, ApplicationStatus status, Pageable pageable);

    Page<Application> findByOwnerAndCompanyNameIgnoreCase(User owner, String companyName, Pageable pageable);

    List<Application> findByStatus(ApplicationStatus status);

    long countByOwnerAndStatus(User owner, ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.owner = :owner " +
            "AND (:status IS NULL OR a.status = :status) " +
            "AND (:companyName IS NULL OR LOWER(a.companyName) LIKE LOWER(CONCAT('%', :companyName, '%')))")
    Page<Application> searchApplications(
            @Param("owner") User owner,
            @Param("status") ApplicationStatus status,
            @Param("companyName") String companyName,
            Pageable pageable
    );

    @Query("SELECT a FROM Application a WHERE a.owner = :owner AND a.dateApplied BETWEEN :startDate AND :endDate")
    List<Application> findApplicationsInDateRange(
            @Param("owner") User owner,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
