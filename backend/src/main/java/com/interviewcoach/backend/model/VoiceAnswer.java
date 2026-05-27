package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voice_answers", indexes = {
    @Index(name = "idx_voice_answer_session", columnList = "session_id")
})
public class VoiceAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private VoiceInterviewSession session;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "word_count")
    private Integer wordCount;

    @Column(name = "filler_word_count")
    private Integer fillerWordCount;

    @Column(name = "speaking_rate_wpm")
    private Double speakingRateWpm;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "communication_score")
    private Integer communicationScore;

    @Column(name = "clarity_score")
    private Integer clarityScore;

    @Column(name = "grammar_score")
    private Integer grammarScore;

    @Column(name = "technical_score")
    private Integer technicalScore;

    @Column(name = "star_score")
    private Integer starScore;

    @Column(name = "completeness_score")
    private Integer completenessScore;

    @Column(name = "ai_overall_score")
    private Integer overallScore;

    @Column(name = "model_answer", columnDefinition = "TEXT")
    private String modelAnswer;

    @Column(name = "interviewer_feedback", columnDefinition = "TEXT")
    private String interviewerFeedback;

    @Column(name = "strengths_json", columnDefinition = "TEXT")
    private String strengthsJson;

    @Column(name = "improvements_json", columnDefinition = "TEXT")
    private String improvementsJson;

    @Column(name = "missing_points_json", columnDefinition = "TEXT")
    private String missingPointsJson;

    @Column(name = "next_question", columnDefinition = "TEXT")
    private String nextQuestion;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
