package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SkillRadarResponse {
    private int dsaProficiency;
    private int javaProficiency;
    private int sqlProficiency;
    private int reactProficiency;
    private int communicationScore;
    private int problemSolvingScore;
    private int confidenceScore;
    private int hrScore;
}
