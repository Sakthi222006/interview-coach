package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.CompanyProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileResponse {
    private Long id;
    private String companyName;
    private String description;
    private CompanyProfile.DifficultyLevel difficulty;
    private String hiringPattern;
    private List<String> interviewRounds;
    private Double aptitudeWeightage;
    private Double codingWeightage;
    private Double communicationWeightage;
    private Double technicalWeightage;
    private List<String> focusTechnologies;
}
