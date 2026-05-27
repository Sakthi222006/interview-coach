package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VoiceAnswerResponse {

    private Long answerId;
    private Long sessionId;
    private String transcript;
    private Integer wordCount;
    private Integer fillerWordCount;
    private Double speakingRateWpm;
    private Integer confidenceScore;
    private Integer communicationScore;
    private Integer clarityScore;
    private Integer grammarScore;
    private Integer technicalScore;
    private Integer starScore;
    private Integer completenessScore;
    private Integer overallScore;
    private String modelAnswer;
    private String interviewerFeedback;
    private List<String> strengths;
    private List<String> improvements;
    private List<String> missingPoints;
    private String nextQuestion;
    private LocalDateTime createdAt;
}
