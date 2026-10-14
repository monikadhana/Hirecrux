package com.hirecrux_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterviewDashboardResponse {
    private long todayInterviews;
    private long upcomingInterviews;
    private long completedInterviews;
    private long scheduledCount;
    private long acceptedCount;
    private long rejectedCount;
    private long cancelledCount;
}
