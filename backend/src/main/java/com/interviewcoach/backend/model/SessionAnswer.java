package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "session_answers", indexes = {
    @Index(name = "idx_answer_session", columnList = "session_id"),
    @Index(name = "idx_answer_question", columnList = "question_id")
})
public class SessionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // What the user typed or selected
    @Column(name = "user_answer", columnDefinition = "TEXT")
    private String userAnswer;

    @Column(name = "is_correct")
    @Builder.Default
    private Boolean isCorrect = false;

    // AI-generated feedback — populated in Phase 5
    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    // 0.0 to 1.0 — populated in Phase 5
    @Column(name = "confidence_score")
    @Builder.Default
    private Double confidenceScore = 0.0;

    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Integer timeSpentSeconds = 0;

    // ── Phase 5: AI Evaluation Fields ──────────────────────────

    @Column(name = "ai_score")
    private Integer aiScore;

    @Column(name = "technical_score")
    private Integer technicalScore;

    @Column(name = "communication_score")
    private Integer communicationScore;

    @Column(name = "problem_solving_score")
    private Integer problemSolvingScore;

    // Stored as JSON arrays: ["item1","item2"]
    @Column(name = "strengths_json", columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "improvements_json", columnDefinition = "TEXT")
    private String improvementsJson;

    @Column(name = "missing_concepts_json", columnDefinition = "TEXT")
    private String missingConceptsJson;

    @Column(name = "model_answer", columnDefinition = "TEXT")
    private String modelAnswer;

    @Column(name = "interviewer_feedback", columnDefinition = "TEXT")
    private String interviewerFeedback;

    @Column(name = "evaluated_at")
    private LocalDateTime evaluatedAt;

    @Column(name = "ai_evaluated")
    @Builder.Default
    private Boolean aiEvaluated = false;

    @Column(name = "answered_at")
    @Builder.Default
    private LocalDateTime answeredAt = LocalDateTime.now();

    // ── Phase 6: STAR Method Scores (HR questions only) ────────
    @Column(name = "star_situation_score")
    private Integer starSituationScore;

    @Column(name = "star_task_score")
    private Integer starTaskScore;

    @Column(name = "star_action_score")
    private Integer starActionScore;

    @Column(name = "star_result_score")
    private Integer starResultScore;

    @Column(name = "star_total_score")
    private Integer starTotalScore;
}