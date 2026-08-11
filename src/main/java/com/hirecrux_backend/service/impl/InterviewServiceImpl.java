package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.InterviewRequest;
import com.hirecrux_backend.dto.response.InterviewResponse;
import com.hirecrux_backend.entity.Application;
import com.hirecrux_backend.entity.Interview;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.ApplicationStatus;
import com.hirecrux_backend.enums.InterviewMode;
import com.hirecrux_backend.enums.InterviewResult;
import com.hirecrux_backend.enums.InterviewStatus;
import com.hirecrux_backend.exception.ResourceNotFoundException;
import com.hirecrux_backend.repository.ApplicationRepository;
import com.hirecrux_backend.repository.InterviewRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.Module;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public InterviewResponse createInterview(InterviewRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOptional.get();

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
        if(request.getInterviewAt().isBefore(currentTime)){
            throw new RuntimeException("Interview cannot schedule");
        }

        if(request.getInterviewMode() == InterviewMode.ONLINE){
            if(request.getMeetingLink().isEmpty()){
                throw new RuntimeException("Meeting link is required");
            }
        }

        if(request.getInterviewMode() == InterviewMode.OFFLINE){
            if(request.getLocation().isEmpty()){
                throw new RuntimeException("Location is required");
            }
        }

        Interview interview = new Interview();

        interview.setHr(user);
        interview.setApplication(application);
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

        InterviewResponse response = new InterviewResponse();

        response.setInterviewId(savedInterview.getInterviewId());
        response.setApplicationId(application.getApplicationId());
        response.setHrId(user.getUserId());
        response.setInterviewId(savedInterview.getInterviewId());
        response.setInterviewAt(savedInterview.getInterviewAt());
        response.setInterviewMode(savedInterview.getInterviewMode());
        response.setInterviewRound(savedInterview.getInterviewRound());
        response.setInterviewStatus(savedInterview.getInterviewStatus());
        response.setInterviewResult(savedInterview.getInterviewResult());
        response.setMeetingLink(savedInterview.getMeetingLink());
        response.setLocation(savedInterview.getLocation());
        response.setNotes(savedInterview.getNotes());
        return response;
    }
}
