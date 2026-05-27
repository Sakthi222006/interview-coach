package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "aptitude_scores", indexes = {
    @Index(name = "idx_user_aptitude_score", columnList = "user_id"),
    @Index(name = "idx_company_aptitude_score", columnList = "company_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AptitudeScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyProfile company;

    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer skipped;
    private Double scorePercentage;

    private Double quantitativeScore;
    private Double logicalReasoningScore;
    private Double verbalAbilityScore;
    private Double dataInterpretationScore;

    @Column(length = 2000)
    private String weakTopics; // JSON format

    @Column(length = 2000)
    private String strongTopics; // JSON format

    private Integer timeTaken; // in seconds

    @Column(length = 1000)
    private String feedback;

    @Enumerated(EnumType.STRING)
    private Status status; // IN_PROGRESS, COMPLETED, ABANDONED

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public enum Status {
        IN_PROGRESS, COMPLETED, ABANDONED
    }
}
