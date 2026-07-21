package com.hirecrux_backend.dto.response;

import com.hirecrux_backend.enums.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Integer applicationId;
    private Integer jobId;
    private String jobTitle;
    private ApplicationStatus applicationStatus;
    private Integer resumeScore;
    private LocalDateTime appliedAt;
}
