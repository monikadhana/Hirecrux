package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.InterviewRequest;
import com.hirecrux_backend.dto.response.InterviewResponse;
import com.hirecrux_backend.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/interview")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/schedule-interview")
    public ResponseEntity<InterviewResponse> createInterview(@Valid @RequestBody InterviewRequest request){
        InterviewResponse response = interviewService.createInterview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{interviewId}")
    public InterviewResponse getInterviewById(@PathVariable Integer interviewId){
        return interviewService.getInterviewById(interviewId);
    }

    @GetMapping("/getMyInterviews")
    public List<InterviewResponse> getMyInterviews(){
        return interviewService.getMyInterviews();
    }
}
