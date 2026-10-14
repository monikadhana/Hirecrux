package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.Assessment;
import com.hirecrux_backend.entity.AssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, Integer> {
    List<AssessmentQuestion> findByAssessment(Assessment assessment);
}
