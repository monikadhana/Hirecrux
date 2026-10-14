
package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.response.AssessmentResultsResponse;
import com.hirecrux_backend.service.AssessmentResultsService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assessment-results")
public class AssessmentResultsController {

    private final AssessmentResultsService assessmentResultsService;

    @GetMapping("/{assessmentId}")
    public ResponseEntity<List<AssessmentResultsResponse>>
    getAssessmentResults(@PathVariable Integer assessmentId) {

        List<AssessmentResultsResponse> results =
                assessmentResultsService.getAssessmentResults(assessmentId);

        return ResponseEntity.ok(results);
    }
}
