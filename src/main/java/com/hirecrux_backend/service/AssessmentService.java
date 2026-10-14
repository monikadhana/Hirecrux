package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.AssessmentRequest;
import com.hirecrux_backend.dto.response.AssessmentResponse;

public interface AssessmentService {
    AssessmentResponse createAssessment(AssessmentRequest request);
    AssessmentResponse updateAssessment(Integer assessmentId, AssessmentRequest request);
    AssessmentResponse publishAssessment(Integer assessmentId);
    AssessmentResponse closeAssessment(Integer assessmentId);
}
