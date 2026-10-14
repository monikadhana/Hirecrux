package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.AssessmentRequest;
import com.hirecrux_backend.dto.response.AssessmentResponse;
import com.hirecrux_backend.service.AssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    @PostMapping("/create-assessment")
    public ResponseEntity<AssessmentResponse> createAssessment(@Valid @RequestBody AssessmentRequest request){
        AssessmentResponse response = assessmentService.createAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update-assessment/{assessmentId}")
    public ResponseEntity<AssessmentResponse> updateAssessment(@PathVariable Integer assessmentId, @Valid @RequestBody AssessmentRequest request){
        AssessmentResponse response = assessmentService.updateAssessment(assessmentId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/publish-assessment/{assessmentId}")
    public ResponseEntity<AssessmentResponse> publishAssessment(@PathVariable Integer assessmentId){
        AssessmentResponse response = assessmentService.publishAssessment(assessmentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/close-assessment/{assessmentId}")
    public ResponseEntity<AssessmentResponse> closeAssessment(@PathVariable Integer assessmentId){
        AssessmentResponse response = assessmentService.closeAssessment(assessmentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
