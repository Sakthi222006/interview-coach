package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.CompanyProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingChallengeResponse {
    private Long id;
    private String title;
    private String description;
    private CompanyProfile.DifficultyLevel difficulty;
    private String topic;
    private String exampleInput;
    private String exampleOutput;
    private String constraints;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer acceptanceRate;
    private String relatedTopics;
}
