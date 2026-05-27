package com.interviewcoach.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResumeReadinessResponse {
    private String overallReadiness;
    private Integer readinessScore;
    private List<String> keyStrengths;
    private List<String> improvementAreas;
    private List<String> nextSteps;
}
