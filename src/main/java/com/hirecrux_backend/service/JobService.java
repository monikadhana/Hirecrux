package com.hirecrux_backend.service;

import com.hirecrux_backend.dto.request.CreateJobRequest;
import com.hirecrux_backend.dto.response.CreateJobResponse;
import org.springframework.stereotype.Service;

import java.util.List;

public interface JobService {
    CreateJobResponse createJob (CreateJobRequest request);
    List<CreateJobResponse> getAllJobs();
}
