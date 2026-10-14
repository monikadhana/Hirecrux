package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.SubmitAnswerRequest;
import com.hirecrux_backend.dto.response.SubmitAnswerResponse;
import com.hirecrux_backend.service.AssessmentAnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assessment-answers")
public class AssessmentAnswerController {

    private final AssessmentAnswerService assessmentAnswerService;

    @PostMapping("/submit-answer")
    public ResponseEntity<SubmitAnswerResponse> submitAnswer(
            @Valid @RequestBody SubmitAnswerRequest request) {

        SubmitAnswerResponse response =
                assessmentAnswerService.submitAnswer(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}