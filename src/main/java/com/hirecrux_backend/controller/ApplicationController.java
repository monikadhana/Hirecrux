package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.ApplicationRequest;
import com.hirecrux_backend.dto.response.ApplicationResponse;
import com.hirecrux_backend.repository.ApplicationRepository;
import com.hirecrux_backend.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/application")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/createApplication")
    public ApplicationResponse createApplication(@RequestBody ApplicationRequest request) {
        return applicationService.createApplication(request);
    }
}
