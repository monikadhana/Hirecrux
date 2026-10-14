
package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.SubmitAssessmentRequest;
import com.hirecrux_backend.dto.response.SubmitAssessmentResponse;
import com.hirecrux_backend.service.SubmitAssessmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assessment-attempt")
public class SubmitAssessmentController {

    private final SubmitAssessmentService submitAssessmentService;

    @PostMapping("/submit-assessment")
    public ResponseEntity<SubmitAssessmentResponse> submitAssessment(
            @Valid @RequestBody SubmitAssessmentRequest request) {

        SubmitAssessmentResponse response =
                submitAssessmentService.submitAssessment(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
