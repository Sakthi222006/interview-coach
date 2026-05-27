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
public class CompanyMockInterviewResponse {
    private Long companyId;
    private String companyName;
    private String difficulty;
    private List<InterviewRound> rounds;
    private String briefing;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InterviewRound {
        private String title;
        private String description;
        private List<String> focusAreas;
    }
}
