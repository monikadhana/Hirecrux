package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.AssignAssessmentRequest;
import com.hirecrux_backend.dto.response.AssignAssessmentResponse;
import com.hirecrux_backend.service.AssignAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/assign-assessment")
public class AssignAssessmentController {
    private final AssignAssessmentService assignAssessmentService;

    @PostMapping("/assign")
    public ResponseEntity<AssignAssessmentResponse> assignAssessment(@Valid @RequestBody AssignAssessmentRequest request){
        AssignAssessmentResponse response = assignAssessmentService.assignAssessment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my-assessment")
    public ResponseEntity<List<AssignAssessmentResponse>>  getMyAssessments(){
        List<AssignAssessmentResponse> response = assignAssessmentService.getMyAssessments();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
