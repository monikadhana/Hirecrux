package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.SubmitAnswerRequest;
import com.hirecrux_backend.dto.response.SubmitAnswerResponse;

public interface AssessmentAnswerService {
    SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request);
}
