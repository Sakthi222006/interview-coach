package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.RecruiterProfile;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RecruiterProfileResponse {
    private Long id;
    private String recruiterType;
    private String recruiterName;
    private String personality;
    private String interviewStyle;
    private Integer totalInterviews;
    private Integer totalHires;
    private Double averageScore;
    private LocalDateTime createdAt;

    public static RecruiterProfileResponse from(RecruiterProfile profile) {
        return RecruiterProfileResponse.builder()
            .id(profile.getId())
            .recruiterType(profile.getRecruiterType())
            .recruiterName(profile.getRecruiterName())
            .personality(profile.getPersonality())
            .interviewStyle(profile.getInterviewStyle())
            .totalInterviews(profile.getTotalInterviews())
            .totalHires(profile.getTotalHires())
            .averageScore(profile.getAverageScore())
            .createdAt(profile.getCreatedAt())
            .build();
    }
}
