package com.hirecrux_backend.dto.response;

import com.hirecrux_backend.entity.Assessment;
import com.hirecrux_backend.enums.QuestionType;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AssessmentQuestionResponse {
    private Integer questionId;
    private Integer assessmentId;
    private String questionText;
    private QuestionType questionType;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private Integer marks;
    private Integer questionOrder;
    private LocalDateTime createdAt;
}
