package com.hirecrux_backend.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandidateProfileResponse {
    private Integer candidateId;
    private String fullName;
    private String email;
    private String resumeUrl;
    private BigDecimal totalExperience;
    private String education;
    private String linkedinUrl;
    private String githubUrl;
}
