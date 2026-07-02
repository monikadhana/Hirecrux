package com.hirecrux_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "resume_parser_data")
public class ResumeParserData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parser_id", nullable = false)
    private Integer parserId;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "extracted_skills")
    private String extractedSkills;

    @Column(name = "extracted_experience")
    private String extractedExperience;

    @Column(name = "extracted_education")
    private String extractedEducation;

    @Column(name = "extracted_projects")
    private String extractedProjects;

    @Column(name = "parser_score")
    private Integer parserScore;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
