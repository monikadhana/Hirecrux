package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.CreateJobRequest;
import com.hirecrux_backend.dto.response.CreateJobResponse;
import com.hirecrux_backend.entity.Job;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.enums.JobStatus;
import com.hirecrux_backend.repository.JobRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.JobService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.hirecrux_backend.enums.JobStatus.CLOSED;
import static com.hirecrux_backend.enums.JobStatus.OPEN;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Override
    public CreateJobResponse createJob(CreateJobRequest request){
        Job job = new Job();

        //COPY TO ENTITY
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setExperienceRequired(request.getExperienceRequired());
        job.setSalaryRange(request.getSalaryRange());
        job.setLocation(request.getLocation());
        job.setDeadline(request.getDeadline());

        // verifyiing the login user and store in
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        //user may or may not be exist
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
    public List<CreateJobResponse> getAllJobs(){
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

            responses.add(response);
        }
        return responses;
    }

    public CreateJobResponse getJobById(Integer jobId){
        Optional<Job> jobOptional = jobRepository.findById(jobId);
        if(jobOptional.isEmpty()){
            throw new RuntimeException("Job does not exist");
        }
        //1.get the particular job
        Job jobs = jobOptional.get();
        //create new response
        CreateJobResponse response = new CreateJobResponse();
        //copy in response
        response.setJobId(jobs.getJobId());
        response.setTitle(jobs.getTitle());
        response.setDescription(jobs.getDescription());
        response.setExperienceRequired(jobs.getExperienceRequired());
        response.setSalaryRange(jobs.getSalaryRange());
        response.setStatus(jobs.getStatus());
        response.setLocation(jobs.getLocation());
        response.setDeadline(jobs.getDeadline());

        return response;
    }

    public CreateJobResponse updateJobById(CreateJobRequest request, Integer jobId){
        Optional<Job> jobOptional = jobRepository.findById(jobId);

        if(jobOptional.isEmpty()){
            throw new RuntimeException("Job does not exist");
        }

        Job jobs = jobOptional.get();

        if(request.getTitle() != null){
            jobs.setTitle(request.getTitle());
        }
        if(request.getDescription() != null){
            jobs.setDescription(request.getDescription());
        }
        if(request.getExperienceRequired() != null){
            jobs.setExperienceRequired(request.getExperienceRequired());
        }
        if(request.getSalaryRange() != null){
            jobs.setSalaryRange(request.getSalaryRange());
        }
        if(request.getStatus() != null){
            jobs.setStatus(request.getStatus());
        }
        if(request.getLocation() != null){
            jobs.setLocation(request.getLocation());
        }
        if(request.getDeadline() != null){
            jobs.setDeadline(request.getDeadline());
        }

         Job savedJobs = jobRepository.save(jobs);

         CreateJobResponse response = new CreateJobResponse();

         response.setJobId(savedJobs.getJobId());
         response.setTitle(savedJobs.getTitle());
         response.setDescription(savedJobs.getDescription());
         response.setExperienceRequired(savedJobs.getExperienceRequired());
         response.setSalaryRange(savedJobs.getSalaryRange());
         response.setStatus(savedJobs.getStatus());
         response.setLocation(savedJobs.getLocation());
         response.setDeadline(savedJobs.getDeadline());
         return  response;
    }

    public CreateJobResponse closeJobById(Integer jobId){
        Optional<Job> jobOptional = jobRepository.findById(jobId);

        if(jobOptional.isEmpty()){
            throw new RuntimeException("Job does not exist");
        }

        Job jobs = jobOptional.get();
        if(jobs.getStatus() == JobStatus.CLOSED){
            throw new RuntimeException("jobs is already closed");
        }
        jobs.setStatus(JobStatus.CLOSED);

        Job savedStatus = jobRepository.save(jobs);

        CreateJobResponse response = new CreateJobResponse();
        response.setJobId(savedStatus.getJobId());
        response.setTitle(savedStatus.getTitle());
        response.setDescription(savedStatus.getDescription());
        response.setExperienceRequired(savedStatus.getExperienceRequired());
        response.setSalaryRange(savedStatus.getSalaryRange());
        response.setStatus(savedStatus.getStatus());
        response.setStatus(savedStatus.getStatus());
        return response;
    }

    public List<CreateJobResponse> searchJobs(String title, String location, String experienceRequired ){
        //I don't have any filters yet
        Specification<Job> specification = Specification.allOf();

        if(title != null && !title.isBlank()) {
            //allof instead of where, rule - (root,query,criteriaBuilder)
            //root - root.get("title") -- job.title, query - no need, crb - LIKE,WHERE, eg -- WHERE title LIKE '%Java%'
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.like(root.get("title"), "%" + title.toLowerCase() + "%"));
        }
            //WHERE title LIKE '%Java%'
            //AND location LIKE '%Chennai%' -- initially allOf() and? added other

            if(location != null && !location.isBlank() ){
                specification = specification.and(
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.like(root.get("location"), "%"+ location.toLowerCase() +"%"));
            }

            if(experienceRequired != null && !experienceRequired.isBlank()){
                specification = specification.and(
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.like(root.get("experienceRequired"),"%"+ experienceRequired.toLowerCase() +"%"));
            }

            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("status"), JobStatus.OPEN));

            List<Job> jobs = jobRepository.findAll(specification);

            if(jobs.isEmpty()){
                return new ArrayList<>();
            }

            List<CreateJobResponse> responses = new ArrayList<>();
            for(Job searchJobs:jobs){
                CreateJobResponse response = new CreateJobResponse();

                response.setJobId(searchJobs.getJobId());
                response.setTitle(searchJobs.getTitle());
                response.setDescription(searchJobs.getDescription());
                response.setLocation(searchJobs.getLocation());
                response.setExperienceRequired(searchJobs.getExperienceRequired());
                response.setSalaryRange(searchJobs.getSalaryRange());
                response.setDeadline(searchJobs.getDeadline());
                response.setStatus(searchJobs.getStatus());
                responses.add(response);
            }

            return responses;

    }
}
