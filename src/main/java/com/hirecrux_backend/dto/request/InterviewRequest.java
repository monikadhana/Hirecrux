package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.enums.InterviewMode;
import com.hirecrux_backend.enums.InterviewRound;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewRequest {
    @NotNull
    private Integer applicationId;
    @NotNull
    private Integer interviewerId;
    private LocalDateTime interviewAt;
    private InterviewMode interviewMode;
    private InterviewRound interviewRound;
    private String meetingLink;
    private String location;
    private String notes;
}
