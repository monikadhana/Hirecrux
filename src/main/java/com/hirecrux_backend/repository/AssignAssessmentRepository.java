package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.Application;
import com.hirecrux_backend.entity.Assessment;
import com.hirecrux_backend.entity.AssignAssessment;
import com.hirecrux_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignAssessmentRepository extends JpaRepository<AssignAssessment, Integer> {
    boolean existsByAssessmentAndCandidateAndApplication(Assessment assessment, User candidate, Application application);
    List<AssignAssessment> findByCandidate(User candidate);
}
