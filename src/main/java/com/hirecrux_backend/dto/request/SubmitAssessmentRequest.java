package com.hirecrux_backend.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAssessmentRequest {
    @NotNull
    private Integer attemptId;
}
