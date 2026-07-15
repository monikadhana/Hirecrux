package com.hirecrux_backend.dto.request;

import com.hirecrux_backend.enums.JobStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateJobRequest {
    private String title;
    private String description;
    private String experienceRequired;
    private String salaryRange;
    private JobStatus status;
    private String location;
    private LocalDate deadline;
}
