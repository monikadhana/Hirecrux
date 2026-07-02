package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.repository.CandidateProfileRepository;
import com.hirecrux_backend.service.CandidateProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CandidateProfileServiceImpl implements CandidateProfileService {
    private final CandidateProfileRepository candidateProfileRepository;
}
