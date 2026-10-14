package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.AssessmentRequest;
import com.hirecrux_backend.dto.response.AssessmentResponse;
import com.hirecrux_backend.entity.Assessment;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.AssessmentStatus;
import com.hirecrux_backend.enums.UserRole;
import com.hirecrux_backend.exception.InvalidOperationException;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.exception.AccessDeniedException;
import com.hirecrux_backend.repository.AssessmentQuestionRepository;
import com.hirecrux_backend.repository.AssessmentRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.AssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {
    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final UserRepository userRepository;

    @Override
    public AssessmentResponse createAssessment(AssessmentRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User Not Found");
        }

        User user = userOptional.get();
        if(user.getRole() != UserRole.HR){
            throw new AccessDeniedException("Only HR can create assessment");
        }

        Assessment assessment = new Assessment();
        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setDurationMinutes(request.getDurationMinutes());
        assessment.setPassingPercentage(request.getPassingPercentage());
        assessment.setAssessmentType(request.getAssessmentType());
        assessment.setCreatedBy(user);
        assessment.setAssessmentStatus(AssessmentStatus.DRAFT);

        Assessment savedAssessment = assessmentRepository.save(assessment);

        AssessmentResponse assessmentResponse = AssessmentResponse.builder()
                .assessmentId(savedAssessment.getAssessmentId())
                .title(savedAssessment.getTitle())
                .description(savedAssessment.getDescription())
                .durationMinutes(savedAssessment.getDurationMinutes())
                .passingPercentage(savedAssessment.getPassingPercentage())
                .assessmentType(savedAssessment.getAssessmentType())
                .assessmentStatus(savedAssessment.getAssessmentStatus())
                .createdBy(savedAssessment.getCreatedBy().getFullName())
                .createdAt(savedAssessment.getCreatedAt())
                .updatedAt(savedAssessment.getUpdatedAt())
                .build();

        return assessmentResponse;
    }

    @Override
    public AssessmentResponse updateAssessment(Integer assessmentId, AssessmentRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        Optional<Assessment> assessmentOptional = assessmentRepository.findById(assessmentId);
        if(assessmentOptional.isEmpty()){
            throw new ResourceNotFoundException("Assessment not found");
        }
        Assessment assessment = assessmentOptional.get();

        if(user.getRole() != UserRole.HR){
            throw new AccessDeniedException("Only HR can update");
        }
        if (!user.getUserId().equals(assessment.getCreatedBy().getUserId())) {
            throw new AccessDeniedException("Not allowed");
        }

        if(assessment.getAssessmentStatus() != AssessmentStatus.DRAFT){
            throw new InvalidOperationException("Can't Update");
        }
        assessment.setTitle(request.getTitle());
        assessment.setDescription(request.getDescription());
        assessment.setDurationMinutes(request.getDurationMinutes());
        assessment.setPassingPercentage(request.getPassingPercentage());
        assessment.setAssessmentType(request.getAssessmentType());

        Assessment savedAssessment = assessmentRepository.save(assessment);
        AssessmentResponse response = AssessmentResponse.builder()
                .assessmentId(savedAssessment.getAssessmentId())
                .title(savedAssessment.getTitle())
                .description(savedAssessment.getDescription())
                .durationMinutes(savedAssessment.getDurationMinutes())
                .passingPercentage(savedAssessment.getPassingPercentage())
                .assessmentType(savedAssessment.getAssessmentType())
                .assessmentStatus(savedAssessment.getAssessmentStatus())
                .createdBy(savedAssessment.getCreatedBy().getFullName())
                .createdAt(savedAssessment.getCreatedAt())
                .updatedAt(savedAssessment.getUpdatedAt())
                .build();
        return response;
    }

    @Override
    public AssessmentResponse publishAssessment(Integer assessmentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        Optional<Assessment> assessmentOptional = assessmentRepository.findById(assessmentId);
        if(assessmentOptional.isEmpty()){
            throw new ResourceNotFoundException("Assessment not found");
        }
        Assessment assessment = assessmentOptional.get();

        if(user.getRole() != UserRole.HR){
            throw new AccessDeniedException("Only HR can publish");
        }
        if (!user.getUserId().equals(assessment.getCreatedBy().getUserId())){
            throw new AccessDeniedException("Not allowed");
        }

        if(assessment.getAssessmentStatus() == AssessmentStatus.PUBLISHED){
            throw new InvalidOperationException("Already published");
        }

        if(assessment.getAssessmentStatus() != AssessmentStatus.DRAFT){
            throw new InvalidOperationException("Not able to publish");
        }

        // Check whether the assessment contains at least one question
        if (assessmentQuestionRepository.findByAssessment(assessment).isEmpty()) {
            throw new InvalidOperationException(
                    "Cannot publish an assessment without questions");
        }

        assessment.setAssessmentStatus(AssessmentStatus.PUBLISHED);
        Assessment savedAssessment = assessmentRepository.save(assessment);
        AssessmentResponse publishResponse = AssessmentResponse.builder()
                .assessmentId(savedAssessment.getAssessmentId())
                .title(savedAssessment.getTitle())
                .description(savedAssessment.getDescription())
                .durationMinutes(savedAssessment.getDurationMinutes())
                .passingPercentage(savedAssessment.getPassingPercentage())
                .assessmentType(savedAssessment.getAssessmentType())
                .assessmentStatus(savedAssessment.getAssessmentStatus())
                .createdBy(savedAssessment.getCreatedBy().getFullName())
                .createdAt(savedAssessment.getCreatedAt())
                .updatedAt(savedAssessment.getUpdatedAt())
                .build();
        return publishResponse;
    }

    @Override
    public AssessmentResponse closeAssessment(Integer assessmentId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        Optional<Assessment> assessmentOptional = assessmentRepository.findById(assessmentId);
        if(assessmentOptional.isEmpty()){
            throw new ResourceNotFoundException("Assessment not found");
        }
        Assessment assessment = assessmentOptional.get();

        if(user.getRole() != UserRole.HR){
            throw new AccessDeniedException("Only HR can close assessment");
        }
        if (!user.getUserId().equals(assessment.getCreatedBy().getUserId())) {
            throw new AccessDeniedException("Not allowed");
        }

        if(assessment.getAssessmentStatus() == AssessmentStatus.CLOSED){
            throw new InvalidOperationException("Already closed");
        }

        if(assessment.getAssessmentStatus() != AssessmentStatus.PUBLISHED){
            throw new InvalidOperationException("Assessment must be published before closing");
        }

        assessment.setAssessmentStatus(AssessmentStatus.CLOSED);
        Assessment savedAssessment = assessmentRepository.save(assessment);
        AssessmentResponse closeResponse = AssessmentResponse.builder()
                .assessmentId(savedAssessment.getAssessmentId())
                .title(savedAssessment.getTitle())
                .description(savedAssessment.getDescription())
                .durationMinutes(savedAssessment.getDurationMinutes())
                .passingPercentage(savedAssessment.getPassingPercentage())
                .assessmentType(savedAssessment.getAssessmentType())
                .assessmentStatus(savedAssessment.getAssessmentStatus())
                .createdBy(savedAssessment.getCreatedBy().getFullName())
                .createdAt(savedAssessment.getCreatedAt())
                .updatedAt(savedAssessment.getUpdatedAt())
                .build();
        return closeResponse;
    }
}
