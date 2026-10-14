package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.AssessmentAttempt;
import com.hirecrux_backend.entity.AssignAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssessmentAttemptRepository extends JpaRepository<AssessmentAttempt,Integer> {
    Optional<AssessmentAttempt> findByAssignment(AssignAssessment assignAssessment);

    List<AssessmentAttempt>
    findByAssignment_Assessment_AssessmentIdAndStatus(
            Integer assessmentId,
            String status
    );

}
