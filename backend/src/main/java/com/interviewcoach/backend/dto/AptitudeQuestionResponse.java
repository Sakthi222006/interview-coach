package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.AptitudeQuestion;
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
public class AptitudeQuestionResponse {
    private Long id;
    private String question;
    private AptitudeQuestion.Category category;
    private CompanyProfile.DifficultyLevel difficulty;
    private List<String> options;
    private String correctAnswer;
    private String explanation;
    private Integer timeLimit;
    private String topic;
}
