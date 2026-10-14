package com.hirecrux_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SubmitAnswerResponse {
    private Integer answerId;
    private Integer attemptId;
    private Integer questionId;
    private String selectedAnswer;
    private Integer marksObtained;
    private Boolean isCorrect;
    private LocalDateTime answeredAt;
}
