
package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.response.AssessmentResultsResponse;
import com.hirecrux_backend.entity.Assessment;
import com.hirecrux_backend.entity.AssessmentAttempt;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.UserRole;
import com.hirecrux_backend.exception.AccessDeniedException;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.repository.AssessmentAttemptRepository;
import com.hirecrux_backend.repository.AssessmentRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.AssessmentResultsService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssessmentResultsServiceImpl
        implements AssessmentResultsService {

    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final AssessmentRepository assessmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentResultsResponse> getAssessmentResults(
            Integer assessmentId) {

        // 1. Get the authenticated HR user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new AccessDeniedException(
                    "Please log in to view assessment results");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        // 2. Verify the user's role
        if (user.getRole() != UserRole.HR) {
            throw new AccessDeniedException(
                    "Only HR users can view assessment results");
        }

        // 3. Find the assessment
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Assessment not found"));

        // 4. Verify assessment ownership
        if (assessment.getCreatedBy() == null
                || !assessment.getCreatedBy().getUserId()
                .equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "You cannot view results for another HR user's assessment");
        }

        // 5. Retrieve submitted attempts for this assessment
        List<AssessmentAttempt> attempts =
                assessmentAttemptRepository
                        .findByAssignment_Assessment_AssessmentIdAndStatus(
                                assessmentId, "SUBMITTED");

        // 6. Map attempts to response DTOs
        return attempts.stream()
                .map(attempt -> AssessmentResultsResponse.builder()
                        .attemptId(attempt.getAttemptId())
                        .assignmentId(attempt.getAssignment()
                                .getAssignmentId())
                        .candidateId(attempt.getAssignment()
                                .getCandidate().getUserId())
                        .candidateName(attempt.getAssignment()
                                .getCandidate().getFullName())
                        .candidateEmail(attempt.getAssignment()
                                .getCandidate().getEmail())
                        .status(attempt.getStatus())
                        .startedAt(attempt.getStartedAt())
                        .submittedAt(attempt.getSubmittedAt())
                        .score(attempt.getScore())
                        .totalMarks(attempt.getTotalMarks())
                        .percentage(attempt.getPercentage())
                        .result(attempt.getResult())
                        .build())
                .toList();
    }
}
