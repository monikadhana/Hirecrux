package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.ApplicationRequest;
import com.hirecrux_backend.dto.request.UpdateApplicationRequest;
import com.hirecrux_backend.dto.response.ApplicationResponse;
import com.hirecrux_backend.repository.ApplicationRepository;
import com.hirecrux_backend.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/application")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/createApplication")
    public ApplicationResponse createApplication(@Valid @RequestBody ApplicationRequest request) {
        return applicationService.createApplication(request);
    }

    @GetMapping("/{applicationId}")
    public ApplicationResponse getApplicationById(@PathVariable Integer applicationId){
        return applicationService.getApplicationById(applicationId);
    }

    @PutMapping("/update/{applicationId}")
    public ApplicationResponse updateApplicationStatus( @PathVariable Integer applicationId, @RequestBody UpdateApplicationRequest request){
        return applicationService.updateApplicationStatus(applicationId, request);
    }

    @GetMapping("/job/{jobId}")
    public List<ApplicationResponse> getApplicationsByJob(@PathVariable Integer jobId){
        return applicationService.getApplicationsByJob(jobId);
    }
}
