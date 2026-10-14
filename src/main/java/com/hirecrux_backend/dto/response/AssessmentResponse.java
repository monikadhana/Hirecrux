package com.hirecrux_backend.dto.response;

import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.AssessmentStatus;
import com.hirecrux_backend.enums.AssessmentType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class AssessmentResponse {
    private Integer assessmentId;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Integer passingPercentage;
    private AssessmentType assessmentType;
    private AssessmentStatus assessmentStatus;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
