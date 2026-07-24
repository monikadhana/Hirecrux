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

        //copy to entity
        candidateProfile.setResumeUrl(request.getResumeUrl());
        candidateProfile.setEducation(request.getEducation());
        candidateProfile.setTotalExperience(request.getTotalExperience());
        candidateProfile.setLinkedinUrl(request.getLinkedinUrl());
        candidateProfile.setGithubUrl(request.getGithubUrl());

        //Use JWT to get the user -- get userName from userDetails
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        //always get the user using email--findbyemail
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        //check the candidate exist by user
        if(candidateProfileRepository.existsByUser(user)){
            throw new RuntimeException("candidate profile already exists");
        }
        candidateProfile.setUser(user);
        CandidateProfile savedCandidateProfile = candidateProfileRepository.save(candidateProfile);//save repo

        //create response
        CandidateProfileResponse response = new CandidateProfileResponse();

        // copy to response
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

    @Override
    public CandidateProfileResponse getMyProfile(){
        //Use JWT to get the user -- get userName from userDetails
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        //always get the user using email--findbyemail
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        //check the candidate exist by user
        Optional<CandidateProfile> candidateProfileOptional = candidateProfileRepository.findByUser(user);
        if(candidateProfileOptional.isEmpty()){
            throw new RuntimeException("Candidate Profile not found");
        }
        CandidateProfile candidateProfile = candidateProfileOptional.get();

        //create new response and copy the gets candidate to response
        CandidateProfileResponse response = new CandidateProfileResponse();

        response.setCandidateId(candidateProfile.getCandidateId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setEducation(candidateProfile.getEducation());
        response.setTotalExperience(candidateProfile.getTotalExperience());
        response.setLinkedinUrl(candidateProfile.getLinkedinUrl());
        response.setGithubUrl(candidateProfile.getGithubUrl());
        response.setResumeUrl(candidateProfile.getResumeUrl());

        return response;
    }

    @Override
    public CandidateProfileResponse updateMyProfile(CandidateProfileRequest request){
        //Use JWT to get the user -- get userName from userDetails
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();

        //always get the user using email--findbyemail
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        //check the candidate exist by user
        Optional<CandidateProfile> candidateProfileOptional = candidateProfileRepository.findByUser(user);
        if(candidateProfileOptional.isEmpty()){
            throw new RuntimeException("Candidate not found");
        }
        CandidateProfile candidateProfile = candidateProfileOptional.get(); //get candidate
        CandidateProfile updatedCandidateProfile = candidateProfile;  //save the already get to updatecandidate

        if(request.getEducation() != null){
            updatedCandidateProfile.setEducation(request.getEducation());
        }
        if(request.getTotalExperience() != null){
            updatedCandidateProfile.setTotalExperience(request.getTotalExperience());
        }
        if(request.getResumeUrl() != null){
            updatedCandidateProfile.setResumeUrl(request.getResumeUrl());
        }
        if(request.getLinkedinUrl() != null){
            updatedCandidateProfile.setLinkedinUrl(request.getLinkedinUrl());
        }
        if(request.getGithubUrl() != null){
            updatedCandidateProfile.setGithubUrl(request.getGithubUrl());
        }

        CandidateProfile savedCandidate = candidateProfileRepository.save(updatedCandidateProfile); //save in repo

        //create new response and copy the savedCandidate to response
        CandidateProfileResponse response = new CandidateProfileResponse();

        response.setCandidateId(savedCandidate.getCandidateId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setEducation(savedCandidate.getEducation());
        response.setTotalExperience(savedCandidate.getTotalExperience());
        response.setResumeUrl(savedCandidate.getResumeUrl());
        response.setGithubUrl(savedCandidate.getGithubUrl());
        response.setLinkedinUrl(savedCandidate.getLinkedinUrl());

        return response;
    }

    public CandidateProfileResponse getCandidateProfileById(Integer candidateId){
        //find the candidate id using the candidate id
        Optional<CandidateProfile> candidateOptional = candidateProfileRepository.findById(candidateId);

        if(candidateOptional.isEmpty()){
            throw new RuntimeException("Candidate not found");
        }
        CandidateProfile candidateProfile = candidateOptional.get();
        //get the user using candidateProfile...why? because we want to response with fullname and email that is in the user only
        User user = candidateProfile.getUser();

        //create new response and copy the get candidate to response
        CandidateProfileResponse response = new CandidateProfileResponse();

        response.setCandidateId(candidateProfile.getCandidateId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setEducation(candidateProfile.getEducation());
        response.setTotalExperience(candidateProfile.getTotalExperience());
        response.setResumeUrl(candidateProfile.getResumeUrl());
        response.setLinkedinUrl(candidateProfile.getLinkedinUrl());
        response.setGithubUrl(candidateProfile.getGithubUrl());

        return response;
    }
}
