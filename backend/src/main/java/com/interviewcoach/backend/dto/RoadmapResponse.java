package com.interviewcoach.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoadmapResponse {

    private String overallReadiness;
    private int readinessScore;
    private List<RoadmapPhaseResponse> phases;
    private List<RoadmapItem> items;

    @Data
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoadmapPhaseResponse {
        private String title;
        private String description;
        private Integer durationDays;
        private Integer priority;
        private String difficulty;
        private Double estimatedHours;
        private List<RoadmapTaskResponse> tasks;
    }

    @Data
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoadmapTaskResponse {
        private String title;
        private String description;
        private Integer durationDays;
        private Integer priority;
        private String difficulty;
        private Double estimatedHours;
    }

    @Data
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoadmapItem {
        private int priority;
        private String type;
        private String title;
        private String description;
        private String metric;
        private String icon;
    }
}
