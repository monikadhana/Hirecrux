package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.CompleteInterviewRequest;
import com.hirecrux_backend.dto.request.InterviewRequest;
import com.hirecrux_backend.dto.response.InterviewDashboardResponse;
import com.hirecrux_backend.dto.response.InterviewResponse;

import java.util.List;

public interface InterviewService {
    InterviewResponse createInterview(InterviewRequest request);
    InterviewResponse getInterviewById(Integer interviewId);
    List<InterviewResponse> getMyInterviews();
    InterviewResponse acceptInterview(Integer interviewId);
    InterviewResponse rejectInterview(Integer interviewId);
    InterviewResponse completeInterview(Integer interviewId, CompleteInterviewRequest request);
    InterviewResponse cancelInterview(Integer interviewId);
    InterviewDashboardResponse getDashboard();
}
