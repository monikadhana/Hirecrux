package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.CandidateProfileRequest;
import com.hirecrux_backend.dto.response.CandidateProfileResponse;
import com.hirecrux_backend.dto.response.CreateJobResponse;

public interface CandidateProfileService {
    CandidateProfileResponse createCandidateProfile(CandidateProfileRequest request);
    CandidateProfileResponse getMyProfile();
    CandidateProfileResponse updateMyProfile(CandidateProfileRequest request);
    CandidateProfileResponse getCandidateProfileById(Integer candidateId);
}
