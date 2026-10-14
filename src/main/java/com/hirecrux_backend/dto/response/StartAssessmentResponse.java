package com.hirecrux_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StartAssessmentResponse {
    private Integer attemptId;
    private Integer assignmentId;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer durationMinutes;
}
