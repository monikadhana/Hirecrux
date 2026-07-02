package com.hirecrux_backend.service.impl;

import com.hirecrux_backend.repository.JobRepository;
import com.hirecrux_backend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
}
