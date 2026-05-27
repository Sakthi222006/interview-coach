package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VoiceSessionSummaryResponse {

    private Long sessionId;
    private String topic;
    private String targetRole;
    private String difficulty;
    private String status;
    private Integer durationSeconds;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Double averageScore;
    private Double averageConfidence;
    private Double averageCommunication;
    private Double averageClarity;
    private Double averageGrammar;
    private Double averageTechnical;
    private Double averageStar;
    private Double averageCompleteness;
    private Double averageWpm;
    private List<VoiceAnswerResponse> answerHistory;
    private List<String> conversationMemory;
}
