package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.CreateJobRequest;
import com.hirecrux_backend.dto.response.CreateJobResponse;
import com.hirecrux_backend.entity.Job;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.repository.JobRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.security.JwtAuthenticationFilter;
import com.hirecrux_backend.service.JobService;
import jakarta.validation.constraints.Email;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final UserDetailsPasswordService userDetailsPasswordService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
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
            throw new RuntimeException("Invalid Email or password");
        }
        User user = userOptional.get();
    }
}
