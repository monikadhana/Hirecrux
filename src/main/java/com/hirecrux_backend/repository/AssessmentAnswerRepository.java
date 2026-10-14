package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.AssessmentAnswer;
import com.hirecrux_backend.entity.AssessmentAttempt;
import com.hirecrux_backend.entity.AssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentAnswerRepository extends JpaRepository<AssessmentAnswer, Integer> {
    Optional<AssessmentAnswer> findByAttemptAndQuestion(
            AssessmentAttempt attempt,
            AssessmentQuestion question);
    List<AssessmentAnswer> findByAttempt(AssessmentAttempt attempt);
}
