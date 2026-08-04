package com.hirecrux_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CandidateProfileRequest {
    @NotBlank
    private String resumeUrl;
    @NotNull
    private BigDecimal totalExperience;
    @NotBlank
    private String education;
    @NotBlank
    private String linkedinUrl;
    @NotBlank
    private String githubUrl;
}
