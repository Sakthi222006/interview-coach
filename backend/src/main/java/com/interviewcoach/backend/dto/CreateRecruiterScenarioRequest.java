package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRecruiterScenarioRequest {
    private String recruiterType;  // HR_RECRUITER, SENIOR_DEVELOPER, TECH_LEAD, ENGINEERING_MANAGER, SYSTEM_DESIGN_INTERVIEWER
    private String roundType;      // HR_ROUND, TECHNICAL_ROUND, MANAGERIAL_ROUND, SYSTEM_DESIGN_ROUND, BEHAVIORAL_ROUND
    private String title;
    private String scenarioContext;
    private String jobDescription;
    private Integer totalRounds;   // Number of questions
}
