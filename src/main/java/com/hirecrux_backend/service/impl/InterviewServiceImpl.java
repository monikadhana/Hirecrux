package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.InterviewRequest;
import com.hirecrux_backend.dto.response.InterviewResponse;
import com.hirecrux_backend.entity.Application;
import com.hirecrux_backend.entity.Interview;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.*;
import com.hirecrux_backend.exception.InvalidOperationException;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.repository.ApplicationRepository;
import com.hirecrux_backend.repository.InterviewRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Override
    public InterviewResponse createInterview(InterviewRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

        if(user.getRole() != UserRole.HR){
            throw new AccessDeniedException("Only Hr user can schedule an interview");
        }

        Optional<Application> applicationOptional = applicationRepository.findById(request.getApplicationId());
        if(applicationOptional.isEmpty()){
            throw new ResourceNotFoundException("Application not found");
        }
        Application application = applicationOptional.get();

        if(application.getApplicationStatus() != ApplicationStatus.APPLIED){
            throw new ResourceNotFoundException("Application not found");
        }

        Optional<User> interviewerOptional = userRepository.findById(request.getInterviewerId());
        if(interviewerOptional.isEmpty()){
            throw new ResourceNotFoundException("Interviewer not found");
        }
        User interviewer = interviewerOptional.get();

        LocalDateTime currentTime = LocalDateTime.now();
        if(request.getInterviewAt() == null || request.getInterviewAt().isBefore(currentTime)){
            throw new InvalidOperationException("Interview cannot schedule");
        }

        if(request.getInterviewMode() == InterviewMode.ONLINE){
            if(request.getMeetingLink() == null || request.getMeetingLink().isBlank()){
                throw new InvalidOperationException("Meeting link is required");
            }
        }

        if(request.getInterviewMode() == InterviewMode.OFFLINE){
            if(request.getLocation() == null || request.getLocation().isBlank()){
                throw new InvalidOperationException("Location is required");
            }
        }

        Interview interview = new Interview();

        interview.setApplication(application);
        interview.setHr(user);
        interview.setInterviewer(interviewer);
        interview.setInterviewAt(request.getInterviewAt());
        interview.setInterviewMode(request.getInterviewMode());
        interview.setInterviewRound(request.getInterviewRound());
        interview.setMeetingLink(request.getMeetingLink());
        interview.setLocation(request.getLocation());
        interview.setNotes(request.getNotes());
        interview.setInterviewStatus(InterviewStatus.SCHEDULED);
        interview.setInterviewResult(InterviewResult.PENDING);

        Interview savedInterview = interviewRepository.save(interview);

        InterviewResponse response = InterviewResponse.builder()
                .interviewId(savedInterview.getInterviewId())
                .applicationId(savedInterview.getApplication().getApplicationId())
                .hrId(savedInterview.getHr().getUserId())
                .interviewerId(savedInterview.getInterviewer().getUserId())
                .interviewAt(savedInterview.getInterviewAt())
                .interviewMode(savedInterview.getInterviewMode())
                .interviewRound(savedInterview.getInterviewRound())
                .interviewStatus(savedInterview.getInterviewStatus())
                .interviewResult(savedInterview.getInterviewResult())
                .meetingLink(savedInterview.getMeetingLink())
                .location(savedInterview.getLocation())
                .notes(savedInterview.getNotes())
                .createdAt(savedInterview.getCreatedAt())
                .updatedAt(savedInterview.getUpdatedAt())
                .build();
        return response;
    }

    @Override
    public InterviewResponse getInterviewById(Integer interviewId){
        Optional<Interview> interviewOptional = interviewRepository.findById(interviewId);
        if(interviewOptional.isEmpty()){
            throw new ResourceNotFoundException("Interview not found");
        }
        Interview interview = interviewOptional.get();

        InterviewResponse responseInterviewId = InterviewResponse.builder()
                .interviewId(interview.getInterviewId())
                .interviewerId(interview.getInterviewer().getUserId())
                .interviewStatus(interview.getInterviewStatus())
                .interviewRound(interview.getInterviewRound())
                .interviewMode(interview.getInterviewMode())
                .interviewResult(interview.getInterviewResult())
                .interviewAt(interview.getInterviewAt())
                .hrId(interview.getHr().getUserId())
                .applicationId(interview.getApplication().getApplicationId())
                .meetingLink(interview.getMeetingLink())
                .location(interview.getLocation())
                .notes(interview.getNotes())
                .createdAt(interview.getCreatedAt())
                .updatedAt(interview.getUpdatedAt())
                .build();
        return responseInterviewId;
    }

    @Override
    public List<InterviewResponse> getMyInterviews(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User Not found");
        }
        User users = userOptional.get();
        List<Interview> interviews;
        if(users.getRole() == UserRole.HR){
            interviews = interviewRepository.findByHr(users);
        }
        else if(users.getRole() == UserRole.INTERVIEWER) {
            interviews = interviewRepository.findByInterviewer(users);
        }
        else {
            interviews = new ArrayList<>();
        }

        List<InterviewResponse> interviewResponseList = new ArrayList<>();
        for(Interview interview: interviews){
            InterviewResponse response = InterviewResponse.builder()
                    .interviewId(interview.getInterviewId())
                    .applicationId(interview.getApplication().getApplicationId())
                    .hrId(interview.getHr().getUserId())
                    .interviewerId(interview.getInterviewer().getUserId())
                    .interviewAt(interview.getInterviewAt())
                    .interviewMode(interview.getInterviewMode())
                    .interviewRound(interview.getInterviewRound())
                    .interviewStatus(interview.getInterviewStatus())
                    .interviewResult(interview.getInterviewResult())
                    .meetingLink(interview.getMeetingLink())
                    .location(interview.getLocation())
                    .notes(interview.getNotes())
                    .createdAt(interview.getCreatedAt())
                    .updatedAt(interview.getUpdatedAt())
                    .build();

            interviewResponseList.add(response);
        }
        return interviewResponseList;
    }
}
