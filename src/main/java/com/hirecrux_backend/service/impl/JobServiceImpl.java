package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.CreateJobRequest;
import com.hirecrux_backend.dto.response.CreateJobResponse;
import com.hirecrux_backend.entity.Job;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.JobStatus;
import com.hirecrux_backend.repository.JobRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.JobService;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public CreateJobResponse createJob(CreateJobRequest request){
        Job job = new Job();

        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setSalaryRange(request.getSalaryRange());
        job.setLocation(request.getLocation());
        job.setDeadline(request.getDeadline());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(userDetails.getUsername());
        if(userOptional.isEmpty()){
            throw new RuntimeException("Authenticated user not found\"");
        }
        User user = userOptional.get();
        job.setHr(user);
        job.setStatus(JobStatus.OPEN);

        Job savedJob = jobRepository.save(job);

        CreateJobResponse createJobResponse = new CreateJobResponse();

        createJobResponse.setJobId(savedJob.getJobId());
        createJobResponse.setTitle(savedJob.getTitle());
        createJobResponse.setDescription(savedJob.getDescription());
        createJobResponse.setExperienceRequired(savedJob.getExperienceRequired());
        createJobResponse.setSalaryRange(savedJob.getSalaryRange());
        createJobResponse.setStatus(savedJob.getStatus());
        createJobResponse.setLocation(savedJob.getLocation());
        createJobResponse.setDeadline(savedJob.getDeadline());

        return createJobResponse;
    }

    @Override
    List<CreateJobResponse> getAllJobs(){
        List<Job> jobs = jobRepository.findAll();
        if(jobs.isEmpty()){
            throw new RuntimeException("Jobs does not exist");
        }
        List<CreateJobResponse> responses = new ArrayList<>();
        //for(datatype variable : collection)
        for(Job job: jobs){
            CreateJobResponse response = new CreateJobResponse();
            response.setJobId(job.getJobId());
            response.setTitle(job.getTitle());
            response.setDescription(job.getDescription());
            response.setExperienceRequired(job.getExperienceRequired());
            response.setSalaryRange(job.getSalaryRange());
            response.setStatus(job.getStatus());
            response.setLocation(job.getLocation());
            response.setDeadline(job.getDeadline());
        }

        return response;
    }
}
