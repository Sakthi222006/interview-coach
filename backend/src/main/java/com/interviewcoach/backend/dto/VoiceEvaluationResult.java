package com.interviewcoach.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoiceEvaluationResult {

    private int overallScore;
    private int confidence;
    private int clarity;
    private int communication;
    private int grammar;
    private int technicalQuality;
    private int starScore;
    private int completeness;
    private String modelAnswer;
    private String interviewerFeedback;
    private List<String> strengths;
    private List<String> improvements;
    private List<String> missingPoints;
    private String nextQuestion;
}
