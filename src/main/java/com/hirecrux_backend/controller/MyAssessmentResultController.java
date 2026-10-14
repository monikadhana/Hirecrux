
package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.response.MyAssessmentResultResponse;
import com.hirecrux_backend.service.MyAssessmentResultService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assessment-attempt")
public class MyAssessmentResultController {

    private final MyAssessmentResultService myAssessmentResultService;

    @GetMapping("/my-result/{attemptId}")
    public ResponseEntity<MyAssessmentResultResponse> getMyResult(
            @PathVariable Integer attemptId) {

        MyAssessmentResultResponse response =
                myAssessmentResultService.getMyResult(attemptId);

        return ResponseEntity.ok(response);
    }
}
