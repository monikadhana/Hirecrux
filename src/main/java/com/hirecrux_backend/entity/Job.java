package com.hirecrux_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.hirecrux_backend.enums.JobStatus;
import lombok.Data;

@Entity
@Data
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Integer jobId;

    @ManyToOne
    @JoinColumn(name = "hr_id")
    private User hr;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "experience_required", length = 50)
    private String experienceRequired;

    @Column(name = "salary_range", length = 50)
    private String salaryRange;

    @Column(name = "location",nullable = false, length = 100)
    private String Location;

    @Column(name = "deadline", nullable = false)
    private LocalDateTime deadline;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobStatus Status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
