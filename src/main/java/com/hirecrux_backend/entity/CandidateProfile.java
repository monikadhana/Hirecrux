package com.hirecrux_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Entity
@Data
public class CandidateProfile {
    @OneToOne
    @JoinColumn(name = "candidate_id")
    private User user;

    private String resume_url;

    private Integer total_experience;
    

}
