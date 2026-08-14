package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.InterviewRequest;
import com.hirecrux_backend.dto.response.InterviewResponse;

public interface InterviewService {
    InterviewResponse createInterview(InterviewRequest request);
}
