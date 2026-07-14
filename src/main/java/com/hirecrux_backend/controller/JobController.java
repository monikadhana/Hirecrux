package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.CreateJobRequest;
import com.hirecrux_backend.dto.response.CreateJobResponse;
import com.hirecrux_backend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/job")
public class JobController {

    private final JobService jobService;
    @PostMapping("/create")
    public CreateJobResponse createJob(@RequestBody CreateJobRequest request){
        return jobService.createJob(request);
    }

    //get all jobs..no resquestbody because we donot give any input for getting all jobs
    @GetMapping("/all")
    public List<CreateJobResponse> getAllJobs(){
        return jobService.getAllJobs();

    }
}
