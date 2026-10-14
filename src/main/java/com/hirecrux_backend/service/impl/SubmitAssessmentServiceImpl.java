
package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.SubmitAssessmentRequest;
import com.hirecrux_backend.dto.response.SubmitAssessmentResponse;
import com.hirecrux_backend.entity.AssessmentAnswer;
import com.hirecrux_backend.entity.AssessmentAttempt;
import com.hirecrux_backend.entity.AssessmentQuestion;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.QuestionType;
import com.hirecrux_backend.enums.UserRole;
import com.hirecrux_backend.exception.AccessDeniedException;
import com.hirecrux_backend.exception.InvalidOperationException;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.repository.AssessmentAnswerRepository;
import com.hirecrux_backend.repository.AssessmentAttemptRepository;
import com.hirecrux_backend.repository.AssessmentQuestionRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.SubmitAssessmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmitAssessmentServiceImpl
        implements SubmitAssessmentService {

    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final AssessmentAnswerRepository assessmentAnswerRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubmitAssessmentResponse submitAssessment(
            SubmitAssessmentRequest request) {

        // 1. Get the authenticated user
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException(
                    "Please log in to submit the assessment");
        }

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        // 2. Only candidates can submit assessments
        if (user.getRole() != UserRole.CANDIDATE) {
            throw new AccessDeniedException(
                    "Only candidates can submit assessments");
        }

        // 3. Find the attempt
        AssessmentAttempt attempt =
                assessmentAttemptRepository.findById(request.getAttemptId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Assessment attempt not found"));

        // 4. Verify attempt ownership
        if (!attempt.getAssignment().getCandidate()
                .getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "You cannot submit another candidate's assessment");
        }

        // 5. Verify attempt status
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new InvalidOperationException(
                    "This assessment attempt has already been submitted "
                            + "or is not in progress");
        }

        if (attempt.getStartedAt() == null) {
            throw new InvalidOperationException(
                    "Assessment start time is missing");
        }

        // 6. Get all questions and saved answers
        List<AssessmentQuestion> questions =
                assessmentQuestionRepository.findByAssessment(
                        attempt.getAssignment().getAssessment());

        List<AssessmentAnswer> answers =
                assessmentAnswerRepository.findByAttempt(attempt);

        // 7. Calculate marks from saved answers
        int score = answers.stream()
                .map(AssessmentAnswer::getMarksObtained)
                .filter(marks -> marks != null)
                .mapToInt(Integer::intValue)
                .sum();

        int totalMarks = attempt.getTotalMarks() == null
                ? 0
                : attempt.getTotalMarks();

        if (totalMarks <= 0) {
            throw new InvalidOperationException(
                    "Total assessment marks must be greater than zero");
        }

        // 8. Calculate percentage
        BigDecimal percentage = BigDecimal.valueOf(score)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalMarks),
                        2, RoundingMode.HALF_UP);

        // 9. Check whether any saved TEXT answer needs manual grading
        boolean pendingManualGrading = answers.stream()
                .anyMatch(answer ->
                        answer.getIsCorrect() == null
                                && answer.getQuestion().getQuestionType()
                                == QuestionType.TEXT);

        // 10. Determine the final result
        String result;

        if (pendingManualGrading) {
            result = "PENDING";
        } else {
            int passingPercentage = attempt.getAssignment()
                    .getAssessment().getPassingPercentage();

            result = percentage.compareTo(
                    BigDecimal.valueOf(passingPercentage)) >= 0
                    ? "PASS"
                    : "FAIL";
        }

        // 11. Update and save the attempt
        attempt.setScore(score);
        attempt.setTotalMarks(totalMarks);
        attempt.setPercentage(percentage);
        attempt.setResult(result);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setStatus("SUBMITTED");

        AssessmentAttempt savedAttempt =
                assessmentAttemptRepository.save(attempt);

        // 12. Return the submission response
        return SubmitAssessmentResponse.builder()
                .attemptId(savedAttempt.getAttemptId())
                .assignmentId(savedAttempt.getAssignment()
                        .getAssignmentId())
                .status(savedAttempt.getStatus())
                .startedAt(savedAttempt.getStartedAt())
                .submittedAt(savedAttempt.getSubmittedAt())
                .score(savedAttempt.getScore())
                .totalMarks(savedAttempt.getTotalMarks())
                .percentage(savedAttempt.getPercentage())
                .result(savedAttempt.getResult())
                .build();
    }
}
