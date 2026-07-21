package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.ApplicationRequest;
import com.hirecrux_backend.dto.response.ApplicationResponse;
import com.hirecrux_backend.repository.ApplicationRepository;
import com.hirecrux_backend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController

@RequestMapping("/api/application")
public class ApplicationController {
    private ApplicationService applicationService;

    @PostMapping("/create-application")
    public ApplicationResponse createApplication(ApplicationRequest request){
        return applicationService.createApplication(request);
    }
}
