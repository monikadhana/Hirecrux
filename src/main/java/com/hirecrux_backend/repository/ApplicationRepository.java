package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.Application;
import com.hirecrux_backend.entity.CandidateProfile;
import com.hirecrux_backend.entity.Job;
import com.hirecrux_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Integer> {
    boolean existsByCandidateAndJob(User Candidate, Job job);
    List<Application> findByJob(Job job);
}
