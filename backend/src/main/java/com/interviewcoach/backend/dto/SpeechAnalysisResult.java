package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpeechAnalysisResult {

    private Integer wordCount;
    private Integer fillerWordCount;
    private Double speakingRateWpm;
    private Integer grammarScore;
    private Integer clarityScore;
    private Integer confidenceScore;
    private Integer completenessScore;
}
