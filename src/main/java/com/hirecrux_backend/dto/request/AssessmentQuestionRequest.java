package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AssessmentQuestionRequest {
    @NotBlank
    private String questionText;
    @NotNull
    private QuestionType questionType;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    @NotNull @Positive
    private Integer marks;
    private Integer questionOrder;
}
