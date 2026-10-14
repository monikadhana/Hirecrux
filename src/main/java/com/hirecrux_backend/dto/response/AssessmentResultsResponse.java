
package com.hirecrux_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class AssessmentResultsResponse {

    private Integer attemptId;
    private Integer assignmentId;

    private Integer candidateId;
    private String candidateName;
    private String candidateEmail;

    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

    private Integer score;
    private Integer totalMarks;
    private BigDecimal percentage;
    private String result;
}
