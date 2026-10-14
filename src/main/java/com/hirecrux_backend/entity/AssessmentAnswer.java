package com.hirecrux_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "assessment_answers")
public class AssessmentAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Integer answerId;

    @ManyToOne
    @JoinColumn(name = "attempt_id", nullable = false)
    private AssessmentAttempt attempt;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private AssessmentQuestion question;

    @Column(name = "selected_answer")
    private String selectedAnswer;

    @Column(name = "marks_obtained")
    private Integer marksObtained = 0;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @Column(name = "answered_at", insertable = false, updatable = false)
    private LocalDateTime answeredAt;
}
