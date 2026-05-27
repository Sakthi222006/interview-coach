package com.interviewcoach.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyReadinessResponse {
    private Long companyId;
    private String companyName;
    private Integer readinessScore;
    private String readinessLevel;
    private Integer resumeScore;
    private Integer aptitudeScore;
    private Integer codingScore;
    private Integer communicationScore;
    private Integer interviewScore;
    private String summary;
    private List<String> strengths;
    private List<String> improvementAreas;
}
