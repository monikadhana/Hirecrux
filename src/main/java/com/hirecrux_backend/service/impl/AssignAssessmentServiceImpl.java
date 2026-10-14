package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.AssignAssessmentRequest;
import com.hirecrux_backend.dto.response.AssignAssessmentResponse;
import com.hirecrux_backend.entity.Application;
import com.hirecrux_backend.entity.Assessment;
import com.hirecrux_backend.entity.AssignAssessment;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.AssessmentStatus;
import com.hirecrux_backend.enums.UserRole;
import com.hirecrux_backend.exception.AccessDeniedException;
import com.hirecrux_backend.exception.DuplicateResourceException;
import com.hirecrux_backend.exception.InvalidOperationException;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.repository.*;
import com.hirecrux_backend.service.AssignAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignAssessmentServiceImpl implements AssignAssessmentService {
    private final AssignAssessmentRepository assignAssessmentRepository;
    private final UserRepository userRepository;
    private final AssessmentRepository assessmentRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    public AssignAssessmentResponse assignAssessment(AssignAssessmentRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();
        if(user.getRole() != UserRole.HR){
            throw new AccessDeniedException("Only HR can Assign the assessment");
        }

        Optional<Assessment> assessmentOptional = assessmentRepository.findById(request.getAssessmentId());
        if(assessmentOptional.isEmpty()){
            throw new ResourceNotFoundException("Assessment not found");
        }
        Assessment assessment = assessmentOptional.get();
        if(assessment.getAssessmentStatus() != AssessmentStatus.PUBLISHED){
            throw new InvalidOperationException("Only published assessment can be assigned");
        }
        if(!assessment.getCreatedBy().getUserId().equals(user.getUserId())){
            throw new AccessDeniedException(
                    "You can assign only your own assessments");
        }

        Optional<User> candidateOptional = userRepository.findById(request.getCandidateId());
        if(candidateOptional.isEmpty()){
            throw new ResourceNotFoundException("Candidate not found");
        }
        User candidate = candidateOptional.get();
        if(candidate.getRole() != UserRole.CANDIDATE){
            throw new RuntimeException("Assessments can only be assigned to candidates");
        }

        Optional<Application> applicationOptional = applicationRepository.findById(request.getApplicationId());
        if(applicationOptional.isEmpty()){
            throw new ResourceNotFoundException("Application does not exist");
        }
        Application application = applicationOptional.get();
        if(!application.getCandidate().getUserId()
                .equals(candidate.getUserId())){
            throw new InvalidOperationException(
                    "Application does not belong to the selected candidate");
        }
        if(assignAssessmentRepository.existsByAssessmentAndCandidateAndApplication(assessment,candidate,application)){
            throw new DuplicateResourceException("Assignment already exist");
        }

        AssignAssessment assignAssessment = new AssignAssessment();
        assignAssessment.setAssessment(assessment);
        assignAssessment.setCandidate(candidate);
        assignAssessment.setApplication(application);
        assignAssessment.setDueAt(request.getDueAt());

        AssignAssessment savedAssignment = assignAssessmentRepository.save(assignAssessment);

        AssignAssessmentResponse response = AssignAssessmentResponse.builder()
                .assignmentId(savedAssignment.getAssignmentId())
                .assessmentId(savedAssignment.getAssessment().getAssessmentId())
                .applicationId(savedAssignment.getApplication().getApplicationId())
                .candidateId(savedAssignment.getCandidate().getUserId())
                .status(savedAssignment.getStatus())
                .dueAt(savedAssignment.getDueAt())
                .assignedAt(savedAssignment.getAssignedAt())
                .build();

        return response;
    }

    @Override
    public List<AssignAssessmentResponse> getMyAssessments() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String email = userDetails.getUsername();

        User candidate = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        if (candidate.getRole() != UserRole.CANDIDATE) {
            throw new AccessDeniedException(
                    "Only candidates can view their assessments");
        }

        List<AssignAssessment> assignments =
                assignAssessmentRepository.findByCandidate(candidate);

        return assignments.stream()
                .map(assignment -> AssignAssessmentResponse.builder()
                        .assignmentId(assignment.getAssignmentId())
                        .assessmentId(
                                assignment.getAssessment().getAssessmentId())
                        .candidateId(
                                assignment.getCandidate().getUserId())
                        .applicationId(
                                assignment.getApplication().getApplicationId())
                        .status(assignment.getStatus())
                        .dueAt(assignment.getDueAt())
                        .assignedAt(assignment.getAssignedAt())
                        .build())
                .toList();
    }


}
