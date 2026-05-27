package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interview_scenarios", indexes = {
    @Index(name = "idx_scenario_recruiter", columnList = "recruiter_profile_id"),
    @Index(name = "idx_scenario_user", columnList = "user_id"),
    @Index(name = "idx_scenario_status", columnList = "status")
})
public class InterviewScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_profile_id", nullable = false)
    private RecruiterProfile recruiterProfile;

    // HR_ROUND / TECHNICAL_ROUND / MANAGERIAL_ROUND / SYSTEM_DESIGN_ROUND / BEHAVIORAL_ROUND
    @Column(nullable = false, length = 30)
    private String roundType;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String scenarioContext;  // Context/background for the interview

    @Column(columnDefinition = "LONGTEXT")
    private String jobDescription;  // Job description for relevance

    // IN_PROGRESS / COMPLETED / ABANDONED
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "IN_PROGRESS";

    @Column(name = "current_round_index")
    @Builder.Default
    private Integer currentRoundIndex = 0;

    @Column(name = "total_rounds")
    @Builder.Default
    private Integer totalRounds = 5;  // Typical number of questions per round

    // Scoring
    @Column(name = "technical_score")
    @Builder.Default
    private Double technicalScore = 0.0;

    @Column(name = "communication_score")
    @Builder.Default
    private Double communicationScore = 0.0;

    @Column(name = "confidence_score")
    @Builder.Default
    private Double confidenceScore = 0.0;

    @Column(name = "leadership_score")
    @Builder.Default
    private Double leadershipScore = 0.0;

    @Column(name = "problem_solving_score")
    @Builder.Default
    private Double problemSolvingScore = 0.0;

    @Column(name = "overall_score")
    @Builder.Default
    private Double overallScore = 0.0;

    // HIRE / NO_HIRE / ON_HOLD
    @Column(length = 20)
    private String hireRecommendation;

    @Column(columnDefinition = "LONGTEXT")
    private String hiringComments;

    @Column(name = "started_at")
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @OneToMany(mappedBy = "scenario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterviewConversation> conversations = new ArrayList<>();
}
