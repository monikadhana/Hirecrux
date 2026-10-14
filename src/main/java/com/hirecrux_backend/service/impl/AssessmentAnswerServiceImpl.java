package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.SubmitAnswerRequest;
import com.hirecrux_backend.dto.response.SubmitAnswerResponse;
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
import com.hirecrux_backend.service.AssessmentAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssessmentAnswerServiceImpl implements AssessmentAnswerService {
    private final AssessmentAnswerRepository assessmentAnswerRepository;
    private final AssessmentAttemptRepository assessmentAttemptRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubmitAnswerResponse submitAnswer(SubmitAnswerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != UserRole.CANDIDATE) {
            throw new AccessDeniedException("Only candidates can submit assessment answers");
        }

        AssessmentAttempt attempt = assessmentAttemptRepository.findById(request.getAttemptId())
                        .orElseThrow(() -> new ResourceNotFoundException("Assessment attempt not found"));

        if (!attempt.getAssignment().getCandidate().getUserId() .equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "You cannot submit answers for another candidate's attempt");
        }

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new InvalidOperationException("Answers can only be submitted for an in-progress assessment");
        }

        LocalDateTime deadline = attempt.getStartedAt()
                .plusMinutes(
                        attempt.getAssignment()
                                .getAssessment()
                                .getDurationMinutes());

        if (!LocalDateTime.now().isBefore(deadline)) {
            throw new InvalidOperationException("The assessment time has expired");
        }

        // 7. Find the question
        AssessmentQuestion question = assessmentQuestionRepository.findById(request.getQuestionId())
                        .orElseThrow(() -> new ResourceNotFoundException("Assessment question not found"));

        if (!question.getAssessment().getAssessmentId()
                .equals(attempt.getAssignment() .getAssessment().getAssessmentId())) {
            throw new InvalidOperationException("This question does not belong to the assigned assessment");
        }

        String selectedAnswer = validateAndNormalizeAnswer(question, request.getSelectedAnswer());

        Optional<AssessmentAnswer> existingAnswer = assessmentAnswerRepository.findByAttemptAndQuestion(attempt, question);

        AssessmentAnswer answer = existingAnswer.orElseGet(AssessmentAnswer::new);
        answer.setAttempt(attempt);
        answer.setQuestion(question);
        answer.setSelectedAnswer(selectedAnswer);

        if (question.getQuestionType() == QuestionType.TEXT) {
            answer.setMarksObtained(0);
            answer.setIsCorrect(null);
        } else {
            boolean correct = isAnswerCorrect(question, selectedAnswer);
            answer.setIsCorrect(correct);
            answer.setMarksObtained(
                    correct ? question.getMarks() : 0);
        }

        // 12. Save the answer
        AssessmentAnswer savedAnswer = assessmentAnswerRepository.save(answer);

        // 13. Return the response without revealing correctness
        return SubmitAnswerResponse.builder()
                .answerId(savedAnswer.getAnswerId())
                .attemptId(savedAnswer.getAttempt().getAttemptId())
                .questionId(savedAnswer.getQuestion().getQuestionId())
                .selectedAnswer(savedAnswer.getSelectedAnswer())
                .build();
    }

    private String validateAndNormalizeAnswer(AssessmentQuestion question, String selectedAnswer) {
        if (selectedAnswer == null || selectedAnswer.isBlank()) {
            throw new InvalidOperationException("Answer cannot be empty");
        }

        if (question.getQuestionType() == QuestionType.TEXT) {
            return selectedAnswer.trim();
        }

        String[] options = selectedAnswer.split(",");
        Set<String> selectedOptions = Arrays.stream(options)
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (selectedOptions.contains("") || selectedOptions.size() != options.length) {
            throw new InvalidOperationException("Answer contains empty or duplicate options");
        }

        if (question.getQuestionType() == QuestionType.SINGLE_CHOICE
                && selectedOptions.size() != 1) {
            throw new InvalidOperationException("Select exactly one option");
        }

        for (String option : selectedOptions) {
            if (!option.matches("[A-D]")) {
                throw new InvalidOperationException("Answer must contain valid option keys: A, B, C or D");
            }

            if (!optionExists(question, option)) {
                throw new InvalidOperationException("Selected option " + option + " does not exist");
            }
        }

        return selectedOptions.stream()
                .sorted()
                .collect(Collectors.joining(","));
    }

    private boolean optionExists(
            AssessmentQuestion question, String option) {

        return switch (option) {
            case "A" -> question.getOptionA() != null
                    && !question.getOptionA().isBlank();
            case "B" -> question.getOptionB() != null
                    && !question.getOptionB().isBlank();
            case "C" -> question.getOptionC() != null
                    && !question.getOptionC().isBlank();
            case "D" -> question.getOptionD() != null
                    && !question.getOptionD().isBlank();
            default -> false;
        };
    }

    private boolean isAnswerCorrect(
            AssessmentQuestion question, String selectedAnswer) {

        if (question.getCorrectAnswer() == null
                || question.getCorrectAnswer().isBlank()) {
            throw new InvalidOperationException(
                    "The correct answer has not been configured");
        }

        Set<String> correctOptions = Arrays.stream(
                        question.getCorrectAnswer().split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<String> selectedOptions = Arrays.stream(
                        selectedAnswer.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());

        return correctOptions.equals(selectedOptions);
    }
}
