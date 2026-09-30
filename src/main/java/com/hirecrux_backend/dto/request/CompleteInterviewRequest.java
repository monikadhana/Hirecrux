package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.enums.InterviewResult;
import lombok.Data;
import lombok.NonNull;

@Data
public class CompleteInterviewRequest {
    @NonNull
    private InterviewResult interviewResult;
}
