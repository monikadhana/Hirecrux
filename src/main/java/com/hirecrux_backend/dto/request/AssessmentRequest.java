package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.enums.AssessmentType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class AssessmentRequest {
    @NotNull
    private String title;
    private String description;
    @NotNull
    private Integer durationMinutes;
    @NotNull
    private AssessmentType assessmentType;
    @NotNull(message = "Passing percentage is required")
    @Min(value = 0, message = "Passing percentage cannot be less than 0")
    @Max(value = 100, message = "Passing percentage cannot exceed 100")
    private Integer passingPercentage;

}
