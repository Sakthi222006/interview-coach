package com.interviewcoach.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingResultResponse {
    private Long challengeId;
    private String title;
    private Boolean passed;
    private Integer passedTests;
    private Integer totalTests;
    private String feedback;
}
