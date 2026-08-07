package com.hirecrux_backend.entity;

import com.hirecrux_backend.enums.InterviewMode;
import com.hirecrux_backend.enums.InterviewResult;
import com.hirecrux_backend.enums.InterviewRound;
import com.hirecrux_backend.enums.InterviewStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "interviews")
public class Interview {
    @Id
    @Column(name = "interview_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer interviewId;

    @JoinColumn(name = "application_id")
    @ManyToOne
    private Application application;

    @JoinColumn(name = "hr_id")
    @ManyToOne
    private User hr;

    @JoinColumn(name = "interviewer_id")
    @ManyToOne
    private User interviewer;

    @Column(name = "interview_At")
    private LocalDateTime interviewAt;

    @Column(name = "interview_mode")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private InterviewMode interviewMode;

    @Column(name = "interview_round")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private InterviewRound interviewRound;

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "location")
    private String location;

    @Column(name = "notes")
    private String notes;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private InterviewStatus status;

    @Column(name = "result")
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private InterviewResult result;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
