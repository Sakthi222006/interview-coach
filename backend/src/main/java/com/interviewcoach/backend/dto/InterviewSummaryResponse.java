package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InterviewSummaryResponse {
    private Long scenarioId;
    private String roundType;
    private String recruiterName;
    private Double technicalScore;
    private Double communicationScore;
    private Double confidenceScore;
    private Double leadershipScore;
    private Double problemSolvingScore;
    private Double overallScore;
    private String hireRecommendation;
    private String hiringComments;
    private Integer durationSeconds;
    private Integer totalTurns;
    private String keyStrengths;
    private String areasForImprovement;
}
