package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class AnalyticsSummaryResponse {
    private long              totalSessions;
    private long              completedSessions;
    private Double            averageScore;
    private long              totalQuestionsAnswered;
    private Map<String, Long> sessionsByTopic;    // {"DSA": 3, "JAVA": 2}
    private double            practiceHours;       // totalDurationSeconds / 3600
    private String            bestTopic;
    private String            weakestTopic;
}