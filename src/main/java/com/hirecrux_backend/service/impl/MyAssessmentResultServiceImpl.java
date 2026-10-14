
package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.response.MyAssessmentResultResponse;
import com.hirecrux_backend.entity.AssessmentAttempt;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.UserRole;
import com.hirecrux_backend.exception.AccessDeniedException;
import com.hirecrux_backend.exception.InvalidOperationException;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.repository.AssessmentAttemptRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.MyAssessmentResultService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyAssessmentResultServiceImpl
        implements MyAssessmentResultService {

    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public MyAssessmentResultResponse getMyResult(Integer attemptId) {

        // 1. Get the authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException(
                    "Please log in to view your assessment result");
        }

        // 2. Find the logged-in user
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        // 3. Only candidates can view their results
        if (user.getRole() != UserRole.CANDIDATE) {
            throw new AccessDeniedException(
                    "Only candidates can view their assessment results");
        }

        // 4. Find the assessment attempt
        AssessmentAttempt attempt =
                assessmentAttemptRepository.findById(attemptId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Assessment attempt not found"));

        // 5. Verify that the attempt belongs to this candidate
        if (!attempt.getAssignment().getCandidate()
                .getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "You cannot view another candidate's result");
        }

        // 6. Results are available only after submission
        if (!"SUBMITTED".equals(attempt.getStatus())) {
            throw new InvalidOperationException(
                    "Submit the assessment before viewing the result");
        }

        // 7. Map the result to the response DTO
        return MyAssessmentResultResponse.builder()
                .attemptId(attempt.getAttemptId())
                .assignmentId(attempt.getAssignment().getAssignmentId())
                .assessmentTitle(attempt.getAssignment()
                        .getAssessment().getTitle())
                .status(attempt.getStatus())
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .score(attempt.getScore())
                .totalMarks(attempt.getTotalMarks())
                .percentage(attempt.getPercentage())
                .result(attempt.getResult())
                .build();
    }
}
