package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "aptitude_questions", indexes = {
    @Index(name = "idx_company_aptitude", columnList = "company_id"),
    @Index(name = "idx_difficulty_aptitude", columnList = "difficulty"),
    @Index(name = "idx_category_aptitude", columnList = "category")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AptitudeQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyProfile company;

    @Column(nullable = false, length = 1000)
    private String question;

    @Enumerated(EnumType.STRING)
    private Category category; // QUANTITATIVE, LOGICAL_REASONING, VERBAL_ABILITY, DATA_INTERPRETATION

    @Enumerated(EnumType.STRING)
    private CompanyProfile.DifficultyLevel difficulty; // EASY, MEDIUM, HARD

    @Column(length = 500)
    private String optionA;

    @Column(length = 500)
    private String optionB;

    @Column(length = 500)
    private String optionC;

    @Column(length = 500)
    private String optionD;

    @Column(length = 10)
    private String correctAnswer; // A, B, C, D

    @Column(length = 1000)
    private String explanation;

    private Integer timeLimit; // in seconds

    @Column(length = 500)
    private String topic;

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

    public enum Category {
        QUANTITATIVE, LOGICAL_REASONING, VERBAL_ABILITY, DATA_INTERPRETATION
    }
}
