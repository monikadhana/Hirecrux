package com.hirecrux_backend.dto.response;

import com.hirecrux_backend.enums.InterviewMode;
import com.hirecrux_backend.enums.InterviewResult;
import com.hirecrux_backend.enums.InterviewRound;
import com.hirecrux_backend.enums.InterviewStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewResponse {
    private Integer interviewId;
    private Integer applicationId;
    private Integer hrId;
    private Integer interviewerId;
    private LocalDateTime interviewAt;
    private InterviewMode interviewMode;
    private InterviewRound interviewRound;
    private InterviewStatus interviewStatus;
    private InterviewResult interviewResult;
    private String meetingLink;
    private String location;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
