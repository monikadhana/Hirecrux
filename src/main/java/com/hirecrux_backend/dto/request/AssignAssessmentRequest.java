package com.hirecrux_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignAssessmentRequest {
    @NotNull
    private Integer assessmentId;
    @NotNull
    private Integer candidateId;
    @NotNull
    private Integer applicationId;
    private LocalDateTime dueAt;
}
