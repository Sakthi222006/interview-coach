package com.interviewcoach.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.interviewcoach.backend.ai.EvaluationResult;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerResponse {
    private Long    answerId;

    @JsonProperty("isCorrect")
    private boolean isCorrect;
    private String  correctAnswer;   // revealed AFTER submission
    private String  explanation;     // why this answer is correct
    private String  questionType;
    private Double  confidenceScore; // Phase 5: AI-calculated
    private String  aiFeedback;      // Phase 5: AI-generated
    private EvaluationResult evaluation;
}