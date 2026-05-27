package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdaptiveRecommendationResponse {
    private String topic;
    private String recommendedDifficulty;
    private String reason;
    private Double rollingAverage;
    private Integer sessionsSampled;
    private boolean hasHistory;
}
