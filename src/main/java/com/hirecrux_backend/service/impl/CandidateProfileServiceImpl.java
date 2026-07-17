package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.dto.request.CandidateProfileRequest;
import com.hirecrux_backend.dto.response.CandidateProfileResponse;
import com.hirecrux_backend.entity.CandidateProfile;
import com.hirecrux_backend.entity.User;
import com.hirecrux_backend.repository.CandidateProfileRepository;
import com.hirecrux_backend.repository.UserRepository;
import com.hirecrux_backend.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final UserRepository userRepository;

    @Override
    public CandidateProfileResponse createCandidateProfile(CandidateProfileRequest request){
        CandidateProfile candidateProfile = new CandidateProfile();

        candidateProfile.setResumeUrl(request.getResumeUrl());
        candidateProfile.setEducation(request.getEducation());
        candidateProfile.setTotalExperience(request.getTotalExperience());
        candidateProfile.setLinkedinUrl(request.getLinkedinUrl());
        candidateProfile.setGithubUrl(request.getGithubUrl());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        if(candidateProfileRepository.existsByUser(user)){
            throw new RuntimeException("candidate profile already exists");
        }
        candidateProfile.setUser(user);
        CandidateProfile savedCandidateProfile = candidateProfileRepository.save(candidateProfile);

        CandidateProfileResponse response = new CandidateProfileResponse();

        response.setCandidateId(savedCandidateProfile.getCandidateId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setResumeUrl(savedCandidateProfile.getResumeUrl());
        response.setEducation(savedCandidateProfile.getEducation());
        response.setTotalExperience(savedCandidateProfile.getTotalExperience());
        response.setGithubUrl(savedCandidateProfile.getGithubUrl());
        response.setLinkedinUrl(savedCandidateProfile.getLinkedinUrl());

        return response;
    }
}
