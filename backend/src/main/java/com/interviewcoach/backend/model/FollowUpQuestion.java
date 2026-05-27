package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "follow_up_questions", indexes = {
    @Index(name = "idx_followup_scenario", columnList = "scenario_id"),
    @Index(name = "idx_followup_conversation", columnList = "parent_conversation_id")
})
public class FollowUpQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private InterviewScenario scenario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_conversation_id")
    private InterviewConversation parentConversation;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String question;

    // COACHING / CHALLENGE / DEEPER_DIVE / WEAKNESS_PROBE / STRENGTH_PROBE
    @Column(nullable = false, length = 30)
    private String followUpType;

    // WEAK_ANSWER / STRONG_ANSWER / INCOMPLETE_ANSWER / UNCLEAR_ANSWER
    @Column(nullable = false, length = 30)
    private String triggerReason;

    // Difficulty level for this follow-up
    // EASY / MEDIUM / HARD
    @Column(length = 20)
    private String difficultyLevel;

    @Column(columnDefinition = "LONGTEXT")
    private String coachingHint;  // Hint if they struggle

    @Column(nullable = false)
    @Builder.Default
    private Boolean isAnswered = false;

    @Column(columnDefinition = "LONGTEXT")
    private String candidateAnswer;

    @Column(name = "answer_quality_score")
    @Builder.Default
    private Double answerQualityScore = 0.0;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
