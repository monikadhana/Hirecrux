package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.StartAssessmentRequest;
import com.hirecrux_backend.dto.response.StartAssessmentResponse;
import com.hirecrux_backend.service.AssessmentAttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Assessment-attempt")
public class AssessmentAttemptController {
    private final AssessmentAttemptService assessmentAttemptService;

    @PostMapping("/start-assessment")
    public ResponseEntity<StartAssessmentResponse> startAssessment(@Valid @RequestBody StartAssessmentRequest request){
        StartAssessmentResponse response = assessmentAttemptService.startAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
