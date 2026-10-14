package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.StartAssessmentRequest;
import com.hirecrux_backend.dto.response.StartAssessmentResponse;

public interface AssessmentAttemptService {
    StartAssessmentResponse startAssessment(StartAssessmentRequest request);
}
