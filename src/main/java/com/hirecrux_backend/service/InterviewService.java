package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.InterviewRequest;
import com.hirecrux_backend.dto.response.InterviewResponse;

import java.util.List;

public interface InterviewService {
    InterviewResponse createInterview(InterviewRequest request);
    InterviewResponse getInterviewById(Integer interviewId);
    List<InterviewResponse> getMyInterviews();
}
