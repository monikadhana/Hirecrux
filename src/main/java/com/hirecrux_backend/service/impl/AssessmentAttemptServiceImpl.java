package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.StartAssessmentRequest;
import com.hirecrux_backend.dto.response.StartAssessmentResponse;
import com.hirecrux_backend.entity.AssessmentAttempt;
import com.hirecrux_backend.entity.AssignAssessment;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.AssessmentStatus;
import com.hirecrux_backend.enums.UserRole;
import com.hirecrux_backend.exception.AccessDeniedException;
import com.hirecrux_backend.exception.InvalidOperationException;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.repository.AssessmentAttemptRepository;
import com.hirecrux_backend.repository.AssessmentQuestionRepository;
import com.hirecrux_backend.repository.AssignAssessmentRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.AssessmentAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssessmentAttemptServiceImpl implements AssessmentAttemptService {
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final AssignAssessmentRepository assignAssessmentRepository;
    private final UserRepository userRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;

    @Override
    public StartAssessmentResponse startAssessment(StartAssessmentRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();
        if(user.getRole() != UserRole.CANDIDATE){
            throw new AccessDeniedException("Only candidate can attend the assessment");
        }

        AssignAssessment assignAssessment =
                assignAssessmentRepository.findById(request.getAssignmentId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Assignment not found"));

        // 4. Verify that the assignment belongs to this candidate
        if (!assignAssessment.getCandidate().getUserId()
                .equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "You can start only your own assigned assessments");
        }

        // 5. Verify that the assessment is published
        if (assignAssessment.getAssessment().getAssessmentStatus()
                != AssessmentStatus.PUBLISHED) {
            throw new InvalidOperationException(
                    "Only published assessments can be started");
        }

        // 6. Check the assignment deadline, if one exists
        if (assignAssessment.getDueAt() != null
                && !assignAssessment.getDueAt().isAfter(LocalDateTime.now())) {
            throw new InvalidOperationException(
                    "The assessment deadline has passed");
        }

        // 7. Prevent duplicate attempts
        if (assessmentAttemptRepository
                .findByAssignment(assignAssessment).isPresent()) {
            throw new InvalidOperationException(
                    "An attempt already exists for this assignment");
        }

        // 8. Calculate the total marks from the assessment questions
        int totalMarks = assessmentQuestionRepository
                .findByAssessment(assignAssessment.getAssessment())
                .stream()
                .mapToInt(question -> question.getMarks())
                .sum();

        if (totalMarks <= 0) {
            throw new InvalidOperationException(
                    "The assessment must contain questions with valid marks");
        }

        // 9. Create and initialize the attempt
        AssessmentAttempt attempt = new AssessmentAttempt();

        attempt.setAssignment(assignAssessment);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus("IN_PROGRESS");
        attempt.setScore(0);
        attempt.setTotalMarks(totalMarks);

        AssessmentAttempt savedAttempt =
                assessmentAttemptRepository.save(attempt);

        // 10. Build the response
        return StartAssessmentResponse.builder()
                .attemptId(savedAttempt.getAttemptId())
                .assignmentId(
                        savedAttempt.getAssignment().getAssignmentId())
                .status(savedAttempt.getStatus())
                .startedAt(savedAttempt.getStartedAt())
                .submittedAt(savedAttempt.getSubmittedAt())
                .durationMinutes(
                        savedAttempt.getAssignment()
                                .getAssessment()
                                .getDurationMinutes())
                .build();

    }
}
