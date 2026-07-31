package com.hirecrux_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandidateProfileRequest {
    @NotBlank
    private String resumeUrl;
    @NotBlank
    private BigDecimal totalExperience;
    @NotBlank
    private String education;
    @NotBlank
    private String linkedinUrl;
    @NotBlank
    private String githubUrl;
}
