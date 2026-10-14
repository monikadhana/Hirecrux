package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.SubmitAssessmentRequest;
import com.hirecrux_backend.dto.response.SubmitAssessmentResponse;

public interface SubmitAssessmentService {

    SubmitAssessmentResponse submitAssessment(
            SubmitAssessmentRequest request
    );
}
