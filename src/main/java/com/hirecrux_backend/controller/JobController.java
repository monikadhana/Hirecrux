package com.hirecrux_backend.controller;

import com.hirecrux_backend.dto.request.CreateJobRequest;
import com.hirecrux_backend.dto.response.CreateJobResponse;
import com.hirecrux_backend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/job")
public class JobController {

    private final JobService jobService;
    @PostMapping("/create")
    public CreateJobResponse createJob(@Valid @RequestBody CreateJobRequest request){
        return jobService.createJob(request);
    }

    //get all jobs..no resquestbody because we donot give any input for getting all jobs
    @GetMapping("/all")
    public List<CreateJobResponse> getAllJobs(){
        return jobService.getAllJobs();
    }

    @GetMapping("/{jobId}")
    public CreateJobResponse getJobById(@PathVariable Integer jobId){
        return jobService.getJobById(jobId);
    }

    @PutMapping("/update/{jobId}")
    public CreateJobResponse updateJobById(@Valid @RequestBody CreateJobRequest request, @PathVariable Integer jobId){
        return jobService.updateJobById(request, jobId);
    }

    @PutMapping("/close/{jobId}")
    public CreateJobResponse closeJobById(@PathVariable Integer jobId){
        return jobService.closeJobById(jobId);
    }

    @GetMapping("/searchJob")
    public List<CreateJobResponse> searchJobs(@RequestParam(required = false) String title,
                                              @RequestParam(required = false) String location,
                                              @RequestParam(required = false) String experienceRequired){
        return jobService.searchJobs(title,location,experienceRequired);
    }
}
