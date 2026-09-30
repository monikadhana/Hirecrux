package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.Interview;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.InterviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    boolean existsByInterviewer(User Interviewer);
    List<Interview> findByHr(User hr);
    List<Interview> findByInterviewer(User interviewer);
    List<Interview> findByHrAndInterviewAtBetween(
            User hr,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Interview> findByHrAndInterviewAtAfter(
            User hr,
            LocalDateTime interviewAt
    );

    long countByHrAndInterviewStatus(
            User hr,
            InterviewStatus status
    );
}
