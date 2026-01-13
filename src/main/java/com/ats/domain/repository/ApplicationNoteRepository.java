package com.ats.domain.repository;

import com.ats.domain.entity.Application;
import com.ats.domain.entity.ApplicationNote;
import com.ats.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationNoteRepository extends JpaRepository<ApplicationNote, Long> {

    List<ApplicationNote> findByApplicationOrderByCreatedAtDesc(Application application);

    List<ApplicationNote> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    long countByApplication(Application application);
}