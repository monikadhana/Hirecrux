package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.Interview;
import com.hirecrux_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Integer> {
    boolean existsByInterviewer(User Interviewer);
}
