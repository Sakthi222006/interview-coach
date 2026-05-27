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
public class ResumeMatchResponse {
    private String matchedRole;
    private Integer matchScore;
    private String roleFitSummary;
    private List<String> recommendedSkills;
    private List<String> matchHighlights;
    private List<String> suggestedGaps;
}
