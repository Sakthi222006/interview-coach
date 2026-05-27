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
@Table(name = "interview_conversations", indexes = {
    @Index(name = "idx_conversation_scenario", columnList = "scenario_id"),
    @Index(name = "idx_conversation_order", columnList = "scenario_id, turn_number")
})
public class InterviewConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scenario_id", nullable = false)
    private InterviewScenario scenario;

    // Turn number in the interview
    @Column(nullable = false)
    private Integer turnNumber;

    // RECRUITER / CANDIDATE
    @Column(nullable = false, length = 20)
    private String speaker;

    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String message;

    // Recruiter reactions to candidate answer
    @Column(columnDefinition = "LONGTEXT")
    private String recruiterReaction;

    // Quality assessment of the answer
    @Column(columnDefinition = "LONGTEXT")
    private String answerAssessment;

    // Technical accuracy
    @Column(name = "technical_accuracy_score")
    @Builder.Default
    private Double technicalAccuracyScore = 0.0;

    // Communication clarity
    @Column(name = "communication_clarity_score")
    @Builder.Default
    private Double communicationClarityScore = 0.0;

    // Problem-solving approach
    @Column(name = "problem_solving_score")
    @Builder.Default
    private Double problemSolvingScore = 0.0;

    // Confidence level
    @Column(name = "confidence_level_score")
    @Builder.Default
    private Double confidenceLevelScore = 0.0;

    // AI-generated comment about this turn
    @Column(columnDefinition = "LONGTEXT")
    private String aiComment;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "parentConversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FollowUpQuestion> followUpQuestions = new ArrayList<>();
}
