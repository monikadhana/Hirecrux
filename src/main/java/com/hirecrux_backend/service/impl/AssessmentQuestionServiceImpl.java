package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.AssessmentQuestionRequest;
import com.hirecrux_backend.dto.response.AssessmentQuestionResponse;
import com.hirecrux_backend.entity.Assessment;
import com.hirecrux_backend.entity.AssessmentQuestion;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.AssessmentStatus;
import com.hirecrux_backend.enums.QuestionType;
import com.hirecrux_backend.enums.UserRole;
import com.hirecrux_backend.exception.InvalidOperationException;
import com.hirecrux_backend.exception.AccessDeniedException;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.repository.AssessmentQuestionRepository;
import com.hirecrux_backend.repository.AssessmentRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.AssessmentQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssessmentQuestionServiceImpl implements AssessmentQuestionService {
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;

    private boolean isValidOption(String answer) {
        return answer != null &&
                (answer.equals("A")
                        || answer.equals("B")
                        || answer.equals("C")
                        || answer.equals("D"));
    }

    private boolean optionExists(String answer, AssessmentQuestionRequest request) {
        return switch (answer) {
            case "A" -> request.getOptionA() != null
                    && !request.getOptionA().isBlank();
            case "B" -> request.getOptionB() != null
                    && !request.getOptionB().isBlank();
            case "C" -> request.getOptionC() != null
                    && !request.getOptionC().isBlank();
            case "D" -> request.getOptionD() != null
                    && !request.getOptionD().isBlank();
            default -> false;
        };
    }

    private void validateQuestionRequest(AssessmentQuestionRequest request) {
        if (request.getQuestionType() == null) {
            throw new InvalidOperationException("Question type is required");
        }

        if (request.getQuestionType() == QuestionType.SINGLE_CHOICE
                || request.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
            if (request.getOptionA() == null || request.getOptionA().isBlank()
                    || request.getOptionB() == null || request.getOptionB().isBlank()) {
                throw new InvalidOperationException("At least two options are required for a choice question");
            }

            if (request.getCorrectAnswer() == null || request.getCorrectAnswer().isBlank()) {
                throw new InvalidOperationException("Correct answer is required for a choice question");
            }

            if (request.getQuestionType() == QuestionType.SINGLE_CHOICE) {
                String answer = request.getCorrectAnswer().trim();
                if (!isValidOption(answer) || !optionExists(answer, request)) {
                    throw new InvalidOperationException("Correct answer must refer to an existing option A, B, C or D");
                }
                request.setCorrectAnswer(answer);
            } else {
                String[] answers = request.getCorrectAnswer().split(",", -1);
                java.util.Set<String> uniqueAnswers = new java.util.HashSet<>();
                java.util.List<String> normalizedAnswers = new java.util.ArrayList<>();
                for (String answer : answers) {
                    String trimmedAnswer = answer.trim();
                    if (!isValidOption(trimmedAnswer) || !optionExists(trimmedAnswer, request)) {
                        throw new InvalidOperationException("Each correct answer must refer to an existing option A, B, C or D");
                    }
                    if (!uniqueAnswers.add(trimmedAnswer)) {
                        throw new InvalidOperationException("Duplicate correct answers are not allowed");
                    }
                    normalizedAnswers.add(trimmedAnswer);
                }
                request.setCorrectAnswer(String.join(",", normalizedAnswers));
            }
        }
        if (request.getQuestionType() == QuestionType.TEXT) {
            request.setCorrectAnswer(null);
        }// TEXT questions do not require options or a correct answer.
    }

    @Override
    public AssessmentQuestionResponse addQuestion( Integer assessmentId, AssessmentQuestionRequest request) {
        // 1. Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // 2. Find logged-in user
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        // 3. Only HR can add questions
        if (user.getRole() != UserRole.HR) {
            throw new AccessDeniedException("Only HR can add questions");
        }

        // 4. Find assessment
        Optional<Assessment> assessmentOptional = assessmentRepository.findById(assessmentId);
        if (assessmentOptional.isEmpty()) {
            throw new ResourceNotFoundException("Assessment not found");
        }
        Assessment assessment = assessmentOptional.get();

        // 5. Check assessment ownership
        if (!user.getUserId().equals(assessment.getCreatedBy().getUserId())) {
            throw new AccessDeniedException(
                    "You are not allowed to modify this assessment");
        }

        // 6. Questions can only be added while assessment is DRAFT
        if (assessment.getAssessmentStatus() != AssessmentStatus.DRAFT) {
            throw new InvalidOperationException(
                    "Questions can only be added to a draft assessment");
        }

        // 7. Validate question according to question type
        validateQuestionRequest(request);

        // 8. Create new question
        AssessmentQuestion question = new AssessmentQuestion();

        question.setAssessment(assessment);
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());
        if (request.getQuestionType() != QuestionType.TEXT) {
            question.setOptionA(request.getOptionA());
            question.setOptionB(request.getOptionB());
            question.setOptionC(request.getOptionC());
            question.setOptionD(request.getOptionD());
        }
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setMarks(request.getMarks());
        question.setQuestionOrder(request.getQuestionOrder());

        // 9. Save question
        AssessmentQuestion savedQuestion = assessmentQuestionRepository.save(question);

        // 10. Build response
        AssessmentQuestionResponse response = AssessmentQuestionResponse.builder()
                        .questionId(savedQuestion.getQuestionId())
                        .assessmentId(savedQuestion.getAssessment().getAssessmentId())
                        .questionText(savedQuestion.getQuestionText())
                        .questionType(savedQuestion.getQuestionType())
                        .optionA(savedQuestion.getOptionA())
                        .optionB(savedQuestion.getOptionB())
                        .optionC(savedQuestion.getOptionC())
                        .optionD(savedQuestion.getOptionD())
                        .marks(savedQuestion.getMarks())
                        .questionOrder(savedQuestion.getQuestionOrder())
                        .createdAt(savedQuestion.getCreatedAt())
                        .build();

        return response;
    }

    @Override
    public List<AssessmentQuestionResponse> getQuestions(Integer assessmentId) {

        // 1. Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Only HR can view the HR-facing question list
        if (user.getRole() != UserRole.HR) {
            throw new AccessDeniedException("Only HR can view assessment questions");
        }

        // 4. Find assessment
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        // 5. Check assessment ownership
        if (!user.getUserId().equals(assessment.getCreatedBy().getUserId())) {
            throw new AccessDeniedException("You are not allowed to view this assessment's questions");
        }

        // 6. Retrieve all questions belonging to this assessment
        List<AssessmentQuestion> questions = assessmentQuestionRepository.findByAssessment(assessment);

        // 7. Convert entities to response DTOs
        return questions.stream()
                .map(question -> AssessmentQuestionResponse.builder()
                        .questionId(question.getQuestionId())
                        .assessmentId(assessment.getAssessmentId())
                        .questionText(question.getQuestionText())
                        .questionType(question.getQuestionType())
                        .optionA(question.getOptionA())
                        .optionB(question.getOptionB())
                        .optionC(question.getOptionC())
                        .optionD(question.getOptionD())
                        .marks(question.getMarks())
                        .questionOrder(question.getQuestionOrder())
                        .createdAt(question.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public AssessmentQuestionResponse updateQuestion(Integer assessmentId, Integer questionId, AssessmentQuestionRequest request) {
        // 1. Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Only HR can update questions
        if (user.getRole() != UserRole.HR) {
            throw new AccessDeniedException("Only HR can update assessment questions");
        }

        // 4. Find assessment
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        // 5. Check assessment ownership
        if (!user.getUserId().equals(assessment.getCreatedBy().getUserId())) {
            throw new AccessDeniedException("You are not allowed to modify this assessment");
        }

        // 6. Questions can only be updated while assessment is DRAFT
        if (assessment.getAssessmentStatus() != AssessmentStatus.DRAFT) {
            throw new InvalidOperationException("Questions can only be updated in a draft assessment");
        }

        // 7. Find question
        AssessmentQuestion question = assessmentQuestionRepository.findById(questionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // 8. Ensure question belongs to this assessment
        if (!question.getAssessment().getAssessmentId().equals(assessmentId)) {
            throw new ResourceNotFoundException("Question does not belong to this assessment");
        }

        // 9. Validate the updated question
        validateQuestionRequest(request);

        // 10. Update question fields
        question.setQuestionText(request.getQuestionText());
        question.setQuestionType(request.getQuestionType());

        if (request.getQuestionType() == QuestionType.TEXT) {
            // Clear old options if changing a choice question to TEXT
            question.setOptionA(null);
            question.setOptionB(null);
            question.setOptionC(null);
            question.setOptionD(null);
        } else {
            question.setOptionA(request.getOptionA());
            question.setOptionB(request.getOptionB());
            question.setOptionC(request.getOptionC());
            question.setOptionD(request.getOptionD());
        }

        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setMarks(request.getMarks());
        question.setQuestionOrder(request.getQuestionOrder());

        // 11. Save updated question
        AssessmentQuestion updatedQuestion = assessmentQuestionRepository.save(question);

        // 12. Return response without correctAnswer
        return AssessmentQuestionResponse.builder()
                .questionId(updatedQuestion.getQuestionId())
                .assessmentId(assessment.getAssessmentId())
                .questionText(updatedQuestion.getQuestionText())
                .questionType(updatedQuestion.getQuestionType())
                .optionA(updatedQuestion.getOptionA())
                .optionB(updatedQuestion.getOptionB())
                .optionC(updatedQuestion.getOptionC())
                .optionD(updatedQuestion.getOptionD())
                .marks(updatedQuestion.getMarks())
                .questionOrder(updatedQuestion.getQuestionOrder())
                .createdAt(updatedQuestion.getCreatedAt())
                .build();
    }

    @Override
    public void deleteQuestion(Integer assessmentId, Integer questionId) {
        // 1. Get logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        // 2. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 3. Only HR can delete questions
        if (user.getRole() != UserRole.HR) {
            throw new AccessDeniedException("Only HR can delete assessment questions");
        }

        // 4. Find assessment
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        // 5. Verify assessment ownership
        if (!user.getUserId().equals(assessment.getCreatedBy().getUserId())) {
            throw new AccessDeniedException("You are not allowed to modify this assessment");
        }

        // 6. Questions can only be deleted while assessment is DRAFT
        if (assessment.getAssessmentStatus() != AssessmentStatus.DRAFT) {
            throw new InvalidOperationException("Questions can only be deleted from a draft assessment");
        }

        // 7. Find question
        AssessmentQuestion question = assessmentQuestionRepository.findById(questionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Question not found"));

        // 8. Ensure question belongs to this assessment
        if (!question.getAssessment().getAssessmentId().equals(assessmentId)) {
            throw new ResourceNotFoundException("Question does not belong to this assessment");
        }

        // 9. Delete question
        assessmentQuestionRepository.delete(question);
    }

}
