package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "voice_interview_results", indexes = {
    @Index(name = "idx_voice_result_session", columnList = "session_id")
})
public class VoiceInterviewResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private VoiceInterviewSession session;

    @Column(name = "average_score")
    private Double averageScore;

    @Column(name = "average_confidence")
    private Double averageConfidence;

    @Column(name = "average_communication")
    private Double averageCommunication;

    @Column(name = "average_clarity")
    private Double averageClarity;

    @Column(name = "average_grammar")
    private Double averageGrammar;

    @Column(name = "average_technical")
    private Double averageTechnical;

    @Column(name = "average_star")
    private Double averageStar;

    @Column(name = "average_completeness")
    private Double averageCompleteness;

    @Column(name = "average_wpm")
    private Double averageWpm;

    @Column(name = "confidence_trend", columnDefinition = "TEXT")
    private String confidenceTrendJson;

    @Column(name = "communication_trend", columnDefinition = "TEXT")
    private String communicationTrendJson;

    @Column(name = "improvement_history", columnDefinition = "TEXT")
    private String improvementHistoryJson;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
