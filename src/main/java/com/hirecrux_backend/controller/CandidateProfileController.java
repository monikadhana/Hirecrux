package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.CandidateProfileRequest;
import com.hirecrux_backend.dto.response.CandidateProfileResponse;
import com.hirecrux_backend.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidate-profile")
public class CandidateProfileController {
    private final CandidateProfileService candidateProfileService;

    @PostMapping("/create")
    public CandidateProfileResponse createCandidateProfile(@RequestBody CandidateProfileRequest request){
        return candidateProfileService.createCandidateProfile(request);
    }
}
