package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.repository.ResumeParserDataRepository;
import com.hirecrux_backend.service.ResumeParserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResumeParserDataServiceImpl implements ResumeParserDataService {
    private final ResumeParserDataRepository resumeParserDataRepository;
}
