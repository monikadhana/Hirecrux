package com.hirecrux_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssignAssessmentResponse {
    private Integer assignmentId;
    private Integer assessmentId;
    private Integer candidateId;
    private Integer applicationId;
    private String status;
    private LocalDateTime dueAt;
    private LocalDateTime assignedAt;
}
