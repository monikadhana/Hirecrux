package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.AssessmentQuestionRequest;
import com.hirecrux_backend.dto.response.AssessmentQuestionResponse;

import java.util.List;

public interface AssessmentQuestionService {
    AssessmentQuestionResponse addQuestion(Integer assessmentId, AssessmentQuestionRequest request);
    List<AssessmentQuestionResponse> getQuestions(Integer assessmentId);
    AssessmentQuestionResponse updateQuestion(Integer assessmentId, Integer questionId, AssessmentQuestionRequest request);
    void deleteQuestion(Integer assessmentId, Integer questionId);
}
