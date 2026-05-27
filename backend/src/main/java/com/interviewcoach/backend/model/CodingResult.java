package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coding_results", indexes = {
    @Index(name = "idx_result_submission", columnList = "submission_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false)
    private CodingSubmission submission;

    private Boolean passed;
    private Integer passedTests;
    private Integer totalTests;

    @Column(length = 2000)
    private String details;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
