package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class TopicAnalysisResponse {

    private List<TopicStat> topicStats;
    private List<String> weakTopics;
    private List<String> strongTopics;

    @Data
    @Builder
    public static class TopicStat {
        private String topic;
        private Long sessionsCompleted;
        private Double averageScore;
        private Double rollingAverage;
        private Double bestScore;
        private Double worstScore;
        private String trend;
        private String recommendedDifficulty;
    }
}
