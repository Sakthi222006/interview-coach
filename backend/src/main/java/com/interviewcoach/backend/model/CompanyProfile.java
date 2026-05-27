package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_profiles", indexes = {
    @Index(name = "idx_company_name", columnList = "company_name"),
    @Index(name = "idx_difficulty", columnList = "difficulty")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String companyName;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private DifficultyLevel difficulty; // EASY, MEDIUM, HARD

    @Column(length = 2000)
    private String hiringPattern;

    @Column(length = 2000)
    private String interviewRounds; // JSON format: ["Round 1", "Round 2", ...]

    private Double aptitudeWeightage; // 0-100
    private Double codingWeightage; // 0-100
    private Double communicationWeightage; // 0-100
    private Double technicalWeightage; // 0-100

    @Column(length = 1000)
    private String focusTechnologies; // JSON format

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

    public enum DifficultyLevel {
        EASY, MEDIUM, HARD
    }
}
