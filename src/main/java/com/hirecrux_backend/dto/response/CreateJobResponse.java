package com.hirecrux_backend.dto.response;

import com.hirecrux_backend.enums.JobStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateJobResponse {
    private Integer jobId;
    private String title;
    private String description;
    private String location;
    private String experienceRequired;
    private String salaryRange;
    private JobStatus status;
    private LocalDate deadline;
}
