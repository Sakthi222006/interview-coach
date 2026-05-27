package com.interviewcoach.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeExtractionResult {

    @JsonProperty("skills")
    private List<String> skills;

    @JsonProperty("technologies")
    private List<String> technologies;

    @JsonProperty("frameworks")
    private List<String> frameworks;

    @JsonProperty("databases")
    private List<String> databases;

    @JsonProperty("tools")
    private List<String> tools;

    @JsonProperty("projects")
    private List<String> projects;

    @JsonProperty("domains")
    private List<String> domains;

    @JsonProperty("strengths")
    private List<String> strengths;

    @JsonProperty("weaknesses")
    private List<String> weaknesses;

    @JsonProperty("recommendedRoles")
    private List<String> recommendedRoles;

    @JsonProperty("resumeScore")
    private Integer resumeScore;

    @JsonProperty("confidenceScore")
    private Double confidenceScore;
}
