package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "coding_submissions", indexes = {
    @Index(name = "idx_submission_user", columnList = "user_id"),
    @Index(name = "idx_submission_challenge", columnList = "challenge_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id", nullable = false)
    private CodingChallenge challenge;

    @Column(columnDefinition = "LONGTEXT")
    private String code;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer passedTests;
    private Integer totalTests;

    @Column(length = 2000)
    private String feedback;

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

    public enum Status {
        PENDING, PASSED, FAILED
    }
}
