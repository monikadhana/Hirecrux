package com.hirecrux_backend.dto.request;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubmitAnswerRequest {
    @NotNull
    private Integer attemptId;

    @NotNull
    private Integer questionId;

    @NotBlank
    private String selectedAnswer;
}
