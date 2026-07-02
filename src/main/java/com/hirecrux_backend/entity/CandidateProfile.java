package com.hirecrux_backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name="candidate_profile")
public class CandidateProfile {
    @Id
    @Column(name = "candidate_id")
    private Integer candidateId;

    @OneToOne
    @JoinColumn(name = "candidate_id", insertable = false, updatable = false)
    private User user;

    @Column(name="resume_url", length= 500)
    private String resumeUrl;

    @Column(name= "total_experience")
    private BigDecimal totalExperience;

    @Column(name= "education")
    private String education;

    @Column(name= "linkedin_url", length = 255)
    private String linkedinUrl;

    @Column(name= "github_url", length = 255)
    private String githubUrl;

    @Column(name= "created_at", insertable=false, updatable= false)
    private LocalDateTime createdAt;

    @Column(name= "updated_at", insertable=false, updatable= false)
    private LocalDateTime updatedAt;
}
