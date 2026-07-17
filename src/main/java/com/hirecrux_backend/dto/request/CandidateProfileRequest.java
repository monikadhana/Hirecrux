package com.hirecrux_backend.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandidateProfileRequest {
    private String resumeUrl;
    private BigDecimal totalExperience;
    private String education;
    private String linkedinUrl;
    private String githubUrl;
}
