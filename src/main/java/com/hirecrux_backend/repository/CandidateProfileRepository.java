package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.CandidateProfile;
import com.hirecrux_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, Integer> {
    boolean existsByUser(User user);
    Optional<CandidateProfile> findByUser(User user);
}
