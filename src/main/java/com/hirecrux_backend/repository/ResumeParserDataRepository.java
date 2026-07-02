package com.hirecrux_backend.repository;

import com.hirecrux_backend.entity.ResumeParserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeParserDataRepository extends JpaRepository<ResumeParserData, Integer> {
}
