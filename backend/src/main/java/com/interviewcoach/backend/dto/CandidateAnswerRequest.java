package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CandidateAnswerRequest {
    private Long scenarioId;
    private String answer;
    private Integer confidenceLevel;  // 1-10
}
