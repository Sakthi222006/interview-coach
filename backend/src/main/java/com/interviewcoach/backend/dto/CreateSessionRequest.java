package com.interviewcoach.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSessionRequest {

    @NotBlank(message = "Topic is required")
    private String topic;      // DSA / JAVA / SQL / REACT / HR

    @NotBlank(message = "Difficulty is required")
    private String difficulty; // EASY / MEDIUM / HARD

    @Min(value = 3,  message = "Minimum 3 questions")
    @Max(value = 20, message = "Maximum 20 questions")
    private int totalQuestions;
}