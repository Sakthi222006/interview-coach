package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PerformanceTrendResponse {

    private List<TrendPoint> dataPoints;
    private String trajectory;
    private Double improvementDelta;

    @Data
    @Builder
    public static class TrendPoint {
        private Long sessionId;
        private String topic;
        private String difficulty;
        private Double score;
        private Integer aiOverallScore;
        private LocalDateTime completedAt;
        private Integer durationSeconds;
        private Integer questionCount;
    }
}
