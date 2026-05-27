package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.InterviewScenario;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InterviewScenarioResponse {
    private Long id;
    private String recruiterType;
    private String recruiterName;
    private String roundType;
    private String title;
    private String status;
    private Integer currentRoundIndex;
    private Integer totalRounds;
    private Double technicalScore;
    private Double communicationScore;
    private Double confidenceScore;
    private Double leadershipScore;
    private Double problemSolvingScore;
    private Double overallScore;
    private String hireRecommendation;
    private String hiringComments;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Integer durationSeconds;

    public static InterviewScenarioResponse from(InterviewScenario scenario) {
        return InterviewScenarioResponse.builder()
            .id(scenario.getId())
            .recruiterType(scenario.getRecruiterProfile().getRecruiterType())
            .recruiterName(scenario.getRecruiterProfile().getRecruiterName())
            .roundType(scenario.getRoundType())
            .title(scenario.getTitle())
            .status(scenario.getStatus())
            .currentRoundIndex(scenario.getCurrentRoundIndex())
            .totalRounds(scenario.getTotalRounds())
            .technicalScore(scenario.getTechnicalScore())
            .communicationScore(scenario.getCommunicationScore())
            .confidenceScore(scenario.getConfidenceScore())
            .leadershipScore(scenario.getLeadershipScore())
            .problemSolvingScore(scenario.getProblemSolvingScore())
            .overallScore(scenario.getOverallScore())
            .hireRecommendation(scenario.getHireRecommendation())
            .hiringComments(scenario.getHiringComments())
            .startedAt(scenario.getStartedAt())
            .completedAt(scenario.getCompletedAt())
            .durationSeconds(scenario.getDurationSeconds())
            .build();
    }
}
