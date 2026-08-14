package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.InterviewRequest;
import com.hirecrux_backend.dto.response.InterviewResponse;
import com.hirecrux_backend.entity.Interview;
import com.hirecrux_backend.service.InterviewService;
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

@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;
    @PostMapping("schedule-interview")
    public ResponseEntity<InterviewResponse> createInterview(@Valid @RequestBody InterviewRequest request){
        InterviewResponse response = interviewService.createInterview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
