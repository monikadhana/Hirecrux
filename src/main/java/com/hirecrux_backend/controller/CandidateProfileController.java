package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.CandidateProfileRequest;
import com.hirecrux_backend.dto.response.CandidateProfileResponse;
import com.hirecrux_backend.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidate-profile")
public class CandidateProfileController {
    private final CandidateProfileService candidateProfileService;

    @PostMapping("/create")
    public CandidateProfileResponse createCandidateProfile(@Valid @RequestBody CandidateProfileRequest request){
        return candidateProfileService.createCandidateProfile(request);
    }

    @GetMapping("/myProfile")
    public CandidateProfileResponse getMyProfile(){
        return candidateProfileService.getMyProfile();
    }

    @PutMapping("/updateProfile")
    public CandidateProfileResponse updateMyProfile(@Valid @RequestBody CandidateProfileRequest request){
        return candidateProfileService.updateMyProfile(request);
    }

    @GetMapping("/getCandidateProfile")
    public CandidateProfileResponse getCandidateProfileById(@PathVariable Integer candidateId){
        return candidateProfileService.getCandidateProfileById(candidateId);
    }
}
