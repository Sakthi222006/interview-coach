package com.interviewcoach.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitAnswerRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    private String userAnswer;        // "A", "B", "C", "D" for MCQ or text for TEXT type

    private Integer timeSpentSeconds; // how long user spent on this question
}