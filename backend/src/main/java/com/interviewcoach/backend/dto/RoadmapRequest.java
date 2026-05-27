package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class RoadmapRequest {
    private Long userId;
    private Long resumeId;
    private String targetRole;
    private List<String> weakTopics;
    private List<String> missingConcepts;
    private String analyticsSummary;
}
