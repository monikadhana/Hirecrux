package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.ApplicationRequest;
import com.hirecrux_backend.dto.request.UpdateApplicationRequest;
import com.hirecrux_backend.dto.response.ApplicationResponse;
import com.hirecrux_backend.entity.Application;
import com.hirecrux_backend.entity.CandidateProfile;
import com.hirecrux_backend.entity.Job;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.ApplicationStatus;
import com.hirecrux_backend.enums.JobStatus;
import com.hirecrux_backend.repository.ApplicationRepository;
import com.hirecrux_backend.repository.CandidateProfileRepository;
import com.hirecrux_backend.repository.JobRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public ApplicationResponse createApplication(ApplicationRequest request){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        Optional<Job> jobOptional= jobRepository.findById(request.getJobId());
        if(jobOptional.isEmpty()){
            throw new RuntimeException("Job not exist");
        }
        Job job = jobOptional.get();

        if(job.getStatus() != JobStatus.OPEN){
            throw new RuntimeException("This job is not open for applications.");
        }

        Optional<CandidateProfile> candidateProfileOptional = candidateProfileRepository.findByUser(user);
        if(candidateProfileOptional.isEmpty()){
            throw new RuntimeException("Candidate not found");
        }

        if(applicationRepository.existsByCandidateAndJob(user, job)){
            throw new RuntimeException("Candidate already applied");
        }

        Application application = new Application();
        application.setCandidate(user);
        application.setJob(job);
        application.setApplicationStatus(ApplicationStatus.APPLIED);
        application.setResumeScore(0);

        Application savedApplication = applicationRepository.save(application);

        ApplicationResponse response = new ApplicationResponse();

        response.setApplicationId(savedApplication.getApplicationId());
        response.setJobId(job.getJobId());
        response.setAppliedAt(savedApplication.getAppliedAt());
        response.setJobTitle(job.getTitle());
        response.setApplicationStatus(savedApplication.getApplicationStatus());
        response.setResumeScore(savedApplication.getResumeScore());

        return response;
    }

    @Override
    public ApplicationResponse getApplicationById(Integer applicationId){
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);

        if(applicationOptional.isEmpty()){
            throw new RuntimeException("Application does not exist");
        }
        Application application = applicationOptional.get();
        ApplicationResponse response = new ApplicationResponse();

        response.setApplicationId(application.getApplicationId());
        response.setJobId(application.getJob().getJobId());
        response.setJobTitle(application.getJob().getTitle());
        response.setApplicationStatus(application.getApplicationStatus());
        response.setResumeScore(application.getResumeScore());
        response.setAppliedAt(application.getAppliedAt());

        return response;
    }

    @Override
    public ApplicationResponse updateApplicationStatus(Integer applicationId, UpdateApplicationRequest request){
        Optional<Application> applicationOptional = applicationRepository.findById(applicationId);
        if(applicationOptional.isEmpty()){
            throw new RuntimeException("Application not found");
        }
        Application application = applicationOptional.get();

        if(request.getApplicationStatus() != null){
            application.setApplicationStatus(request.getApplicationStatus());
        }

        Application updatedApplication = applicationRepository.save(application);
        ApplicationResponse response = new ApplicationResponse();

        response.setApplicationId(updatedApplication.getApplicationId());
        response.setJobId(updatedApplication.getJob().getJobId());
        response.setJobTitle(updatedApplication.getJob().getTitle());
        response.setApplicationStatus(updatedApplication.getApplicationStatus());
        response.setAppliedAt(updatedApplication.getAppliedAt());
        response.setResumeScore(updatedApplication.getResumeScore());

        return response;
    }

    @Override
    public List<ApplicationResponse> getApplicationsByJob(Integer jobId){
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if(jobOptional.isEmpty()){
            throw new RuntimeException("Jobs not found");
        }
        Job job = jobOptional.get();
        List<Application> applications = applicationRepository.findByJob(job);
        List<ApplicationResponse> responses = new ArrayList<>();

        for(Application application: applications){
            ApplicationResponse response = new ApplicationResponse();
            response.setApplicationId(application.getApplicationId());
            response.setJobId(application.getJob().getJobId());
            response.setJobTitle(application.getJob().getTitle());
            response.setApplicationStatus(application.getApplicationStatus());
            response.setResumeScore(application.getResumeScore());
            response.setAppliedAt(application.getAppliedAt());
            responses.add(response);
        }
        return responses;
    }
}