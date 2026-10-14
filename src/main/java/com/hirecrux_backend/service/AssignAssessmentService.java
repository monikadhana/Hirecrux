package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.AssignAssessmentRequest;
import com.hirecrux_backend.dto.response.AssignAssessmentResponse;

import java.util.List;

public interface AssignAssessmentService {
    AssignAssessmentResponse assignAssessment(AssignAssessmentRequest request);
    List<AssignAssessmentResponse> getMyAssessments();

}
