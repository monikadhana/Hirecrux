package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.AssessmentQuestionRequest;
import com.hirecrux_backend.dto.response.AssessmentQuestionResponse;
import com.hirecrux_backend.service.AssessmentQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assessmentQuestion")
public class AssessmentQuestionController {
    private final AssessmentQuestionService assessmentQuestionService;

    @PostMapping("add-questions/{assessmentId}")
    public ResponseEntity<AssessmentQuestionResponse> addQuestion(@PathVariable Integer assessmentId, @Valid @RequestBody AssessmentQuestionRequest request){
        AssessmentQuestionResponse response = assessmentQuestionService.addQuestion(assessmentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{assessmentId}/questions")
    public ResponseEntity<List<AssessmentQuestionResponse>> getQuestions(@PathVariable Integer assessmentId) {
        List<AssessmentQuestionResponse> response = assessmentQuestionService.getQuestions(assessmentId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateQuestion/{assessmentId}/{questionId}")
    public ResponseEntity<AssessmentQuestionResponse> updateQuestion(@PathVariable Integer assessmentId,
                                                                     @PathVariable Integer questionId, @Valid @RequestBody AssessmentQuestionRequest request){
        AssessmentQuestionResponse response = assessmentQuestionService.updateQuestion(assessmentId, questionId,request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{assessmentId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Integer assessmentId, @PathVariable Integer questionId) {
        assessmentQuestionService.deleteQuestion(assessmentId, questionId);
        return ResponseEntity.noContent().build();
    }

}
