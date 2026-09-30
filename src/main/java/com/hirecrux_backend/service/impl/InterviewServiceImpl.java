package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.CompleteInterviewRequest;
import com.hirecrux_backend.dto.request.InterviewRequest;
import com.hirecrux_backend.dto.request.NotificationRequest;
import com.hirecrux_backend.dto.response.InterviewDashboardResponse;
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
import com.hirecrux_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class InterviewServiceImpl implements InterviewService {
    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

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

        if(application.getApplicationStatus() != ApplicationStatus.UNDER_REVIEW){
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

        // CREATE NOTIFICATION FOR CANDIDATE
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId( savedInterview .getApplication() .getCandidate() .getUserId() );
        notificationRequest.setTitle("Interview Scheduled");
        notificationRequest.setMessage( "Your interview has been scheduled for " + savedInterview.getInterviewAt()
                        + ". Interview round: "
                        + savedInterview.getInterviewRound()
        );
        notificationService.createNotification(notificationRequest);
        InterviewResponse response = InterviewResponse.builder()
                .interviewId(savedInterview.getInterviewId())
                .applicationId(savedInterview.getApplication().getApplicationId())
                .hrId(savedInterview.getHr().getUserId())
                .candidateId(savedInterview.getApplication().getCandidate().getUserId())
                .candidateName(savedInterview.getApplication().getCandidate().getFullName())
                .candidateEmail(savedInterview.getApplication().getCandidate().getEmail())
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
                .candidateId(interview.getApplication().getCandidate().getUserId())
                .candidateName(interview.getApplication().getCandidate().getFullName())
                .candidateEmail(interview.getApplication().getCandidate().getEmail())
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
                    .candidateId(interview.getApplication().getCandidate().getUserId())
                    .candidateName(interview.getApplication().getCandidate().getFullName())
                    .candidateEmail(interview.getApplication().getCandidate().getEmail())
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

    @Override
    public InterviewResponse acceptInterview(Integer interviewId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User Not found");
        }
        User user = userOptional.get();

        Optional<Interview> interviewOptional = interviewRepository.findById(interviewId);
        if(interviewOptional.isEmpty()){
            throw new ResourceNotFoundException("Interview not found");
        }
        Interview interview = interviewOptional.get();

        if (!interview.getApplication().getCandidate().getUserId()
                .equals(user.getUserId())) {

            throw new AccessDeniedException(
                    "You are not allowed to accept this interview"
            );
        }

        if(interview.getInterviewStatus() != InterviewStatus.SCHEDULED){
            throw new InvalidOperationException(
                    "Only scheduled interviews can be accepted"
            );
        }
        interview.setInterviewStatus(InterviewStatus.ACCEPTED);
        Interview savedInterview = interviewRepository.save(interview);

        // Notification to HR
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId( savedInterview.getHr().getUserId());
        notificationRequest.setTitle("Interview Accepted");
        notificationRequest.setMessage("Candidate has accepted the scheduled interview.");
        notificationService.createNotification(notificationRequest);

        InterviewResponse acceptResponse = InterviewResponse.builder()
                .interviewId(savedInterview.getInterviewId())
                .interviewerId(savedInterview.getInterviewer().getUserId())
                .candidateId(savedInterview.getApplication().getCandidate().getUserId())
                .candidateName(savedInterview.getApplication().getCandidate().getFullName())
                .candidateEmail(savedInterview.getApplication().getCandidate().getEmail())
                .interviewStatus(savedInterview.getInterviewStatus())
                .interviewRound(savedInterview.getInterviewRound())
                .interviewMode(savedInterview.getInterviewMode())
                .interviewResult(savedInterview.getInterviewResult())
                .interviewAt(savedInterview.getInterviewAt())
                .hrId(savedInterview.getHr().getUserId())
                .applicationId(savedInterview.getApplication().getApplicationId())
                .meetingLink(savedInterview.getMeetingLink())
                .location(savedInterview.getLocation())
                .notes(savedInterview.getNotes())
                .createdAt(savedInterview.getCreatedAt())
                .updatedAt(savedInterview.getUpdatedAt())
                .build();
        return acceptResponse;
    }

    @Override
    public InterviewResponse rejectInterview(Integer interviewId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User Not found");
        }
        User user = userOptional.get();

        Optional<Interview> interviewOptional = interviewRepository.findById(interviewId);
        if(interviewOptional.isEmpty()){
            throw new ResourceNotFoundException("Interview not found");
        }
        Interview interview = interviewOptional.get();

        if (!interview.getApplication().getCandidate().getUserId()
                .equals(user.getUserId())) {

            throw new AccessDeniedException(
                    "You are not allowed to reject this interview"
            );
        }

        if(interview.getInterviewStatus() != InterviewStatus.SCHEDULED){
            throw new InvalidOperationException(
                    "Only scheduled interviews can be rejected"
            );
        }
        interview.setInterviewStatus(InterviewStatus.REJECTED);
        Interview savedInterview = interviewRepository.save(interview);

        //notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(savedInterview.getHr().getUserId());
        notificationRequest.setTitle("Interview Rejected");
        notificationRequest.setMessage("Candidate has rejected the scheduled interview.");
        notificationService.createNotification(notificationRequest);

        InterviewResponse rejectResponse = InterviewResponse.builder()
                .interviewId(savedInterview.getInterviewId())
                .interviewerId(savedInterview.getInterviewer().getUserId())
                .candidateId(savedInterview.getApplication().getCandidate().getUserId())
                .candidateName(savedInterview.getApplication().getCandidate().getFullName())
                .candidateEmail(savedInterview.getApplication().getCandidate().getEmail())
                .interviewStatus(savedInterview.getInterviewStatus())
                .interviewRound(savedInterview.getInterviewRound())
                .interviewMode(savedInterview.getInterviewMode())
                .interviewResult(savedInterview.getInterviewResult())
                .interviewAt(savedInterview.getInterviewAt())
                .hrId(savedInterview.getHr().getUserId())
                .applicationId(savedInterview.getApplication().getApplicationId())
                .meetingLink(savedInterview.getMeetingLink())
                .location(savedInterview.getLocation())
                .notes(savedInterview.getNotes())
                .createdAt(savedInterview.getCreatedAt())
                .updatedAt(savedInterview.getUpdatedAt())
                .build();
        return rejectResponse;
    }

    @Override
    public InterviewResponse completeInterview(Integer interviewId, CompleteInterviewRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<Interview> interviewOptional = interviewRepository.findById(interviewId);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();

        if(interviewOptional.isEmpty()){
            throw  new ResourceNotFoundException("Interview not found");
        }
        Interview interview = interviewOptional.get();

        if (!interview.getHr().getUserId().equals(user.getUserId())
                && !interview.getInterviewer().getUserId().equals(user.getUserId())) {

            throw new AccessDeniedException(
                    "You are not allowed to complete this interview"
            );
        }

        if(interview.getInterviewStatus() != InterviewStatus.ACCEPTED){
            throw new InvalidOperationException(
                    "Only accepted interviews can be completed"
            );
        }

        if(request.getInterviewResult() == InterviewResult.PENDING){
            throw new InvalidOperationException(
                    "Interview result cannot be PENDING"
            );
        }

        interview.setInterviewStatus(InterviewStatus.COMPLETED);
        interview.setInterviewResult(request.getInterviewResult());

        Interview savedStatus = interviewRepository.save(interview);
        // notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(savedStatus.getApplication().getCandidate().getUserId());
        notificationRequest.setTitle("Interview Completed");
        notificationRequest.setMessage("Your interview has been completed. Result: " + savedStatus.getInterviewResult());
        notificationService.createNotification(notificationRequest);

        InterviewResponse response = InterviewResponse.builder()
                .interviewId(savedStatus.getInterviewId())
                .interviewerId(savedStatus.getInterviewer().getUserId())
                .candidateId(savedStatus.getApplication().getCandidate().getUserId())
                .candidateName(savedStatus.getApplication().getCandidate().getFullName())
                .candidateEmail(savedStatus.getApplication().getCandidate().getEmail())
                .interviewStatus(savedStatus.getInterviewStatus())
                .interviewRound(savedStatus.getInterviewRound())
                .interviewMode(savedStatus.getInterviewMode())
                .interviewResult(savedStatus.getInterviewResult())
                .interviewAt(savedStatus.getInterviewAt())
                .hrId(savedStatus.getHr().getUserId())
                .applicationId(savedStatus.getApplication().getApplicationId())
                .meetingLink(savedStatus.getMeetingLink())
                .location(savedStatus.getLocation())
                .notes(savedStatus.getNotes())
                .createdAt(savedStatus.getCreatedAt())
                .updatedAt(savedStatus.getUpdatedAt())
                .build();
        return response;
    }

    public InterviewResponse cancelInterview(Integer interviewId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<Interview> interviewOptional = interviewRepository.findById(interviewId);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();

        if(interviewOptional.isEmpty()){
            throw  new ResourceNotFoundException("Interview not found");
        }
        Interview interview = interviewOptional.get();

        if (!interview.getHr().getUserId().equals(user.getUserId())) {
            throw new AccessDeniedException(
                    "Only the HR who scheduled this interview can cancel it"
            );
        }

        if (interview.getInterviewStatus() == InterviewStatus.COMPLETED
                || interview.getInterviewStatus() == InterviewStatus.REJECTED
                || interview.getInterviewStatus() == InterviewStatus.CANCELLED) {

            throw new InvalidOperationException(
                    "This interview cannot be cancelled"
            );
        }

        interview.setInterviewStatus(InterviewStatus.CANCELLED);

        Interview savedStatus = interviewRepository.save(interview);
        //notification
        NotificationRequest notificationRequest = new NotificationRequest();

        notificationRequest.setUserId( savedStatus .getApplication().getCandidate().getUserId());
        notificationRequest.setTitle("Interview Cancelled");
        notificationRequest.setMessage("Your scheduled interview has been cancelled.");
        notificationService.createNotification(notificationRequest);

        InterviewResponse response = InterviewResponse.builder()
                .interviewId(savedStatus.getInterviewId())
                .interviewerId(savedStatus.getInterviewer().getUserId())
                .candidateId(savedStatus.getApplication().getCandidate().getUserId())
                .candidateName(savedStatus.getApplication().getCandidate().getFullName())
                .candidateEmail(savedStatus.getApplication().getCandidate().getEmail())
                .interviewStatus(savedStatus.getInterviewStatus())
                .interviewRound(savedStatus.getInterviewRound())
                .interviewMode(savedStatus.getInterviewMode())
                .interviewResult(savedStatus.getInterviewResult())
                .interviewAt(savedStatus.getInterviewAt())
                .hrId(savedStatus.getHr().getUserId())
                .applicationId(savedStatus.getApplication().getApplicationId())
                .meetingLink(savedStatus.getMeetingLink())
                .location(savedStatus.getLocation())
                .notes(savedStatus.getNotes())
                .createdAt(savedStatus.getCreatedAt())
                .updatedAt(savedStatus.getUpdatedAt())
                .build();
        return response;
    }

    public InterviewDashboardResponse getDashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOptional.get();

        if (user.getRole() != UserRole.HR) {
            throw new AccessDeniedException(
                    "Only HR can access the dashboard"
            );
        }
        User hr = user;
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();

        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        LocalDateTime now = LocalDateTime.now();

        List<Interview> todayInterviews = interviewRepository.findByHrAndInterviewAtBetween(hr,startOfDay,endOfDay);
        List<Interview> upcomingInterviews = interviewRepository.findByHrAndInterviewAtAfter(hr,now);

        long scheduledCount =  interviewRepository.countByHrAndInterviewStatus(hr, InterviewStatus.SCHEDULED);
        long acceptedCount =interviewRepository.countByHrAndInterviewStatus(hr, InterviewStatus.ACCEPTED);
        long rejectedCount =interviewRepository.countByHrAndInterviewStatus(hr, InterviewStatus.REJECTED);
        long cancelledCount =interviewRepository.countByHrAndInterviewStatus(hr, InterviewStatus.CANCELLED);
        long completedCount =interviewRepository.countByHrAndInterviewStatus(hr, InterviewStatus.COMPLETED);

        return InterviewDashboardResponse.builder()
                .todayInterviews(todayInterviews.size())
                .upcomingInterviews((long) upcomingInterviews.size())
                .completedInterviews(completedCount)
                .scheduledCount(scheduledCount)
                .acceptedCount(acceptedCount)
                .rejectedCount(rejectedCount)
                .cancelledCount(cancelledCount)
                .build();

    }
}
