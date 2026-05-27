package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "coding_challenges", indexes = {
    @Index(name = "idx_company_coding", columnList = "company_id"),
    @Index(name = "idx_difficulty_coding", columnList = "difficulty"),
    @Index(name = "idx_topic_coding", columnList = "topic")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingChallenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyProfile company;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private CompanyProfile.DifficultyLevel difficulty; // EASY, MEDIUM, HARD

    @Column(length = 100)
    private String topic; // Arrays, Strings, Linked Lists, Trees, Graphs, DP, System Design

    @Column(length = 1000)
    private String exampleInput;

    @Column(length = 1000)
    private String exampleOutput;

    @Column(length = 2000)
    private String constraints;

    @Column(columnDefinition = "LONGTEXT")
    private String solutionCode; // Reference solution

    @Column(length = 1000)
    private String solutionApproach;

    private Integer timeLimit; // in seconds
    private Integer memoryLimit; // in MB

    @Column(length = 500)
    private String testCases; // JSON format with input/output pairs

    private Integer acceptanceRate;
    private Integer totalSubmissions;
    private Integer totalAccepted;

    @Column(length = 500)
    private String relatedTopics; // JSON format

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
