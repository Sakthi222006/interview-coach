package com.interviewcoach.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VoiceInterviewStartRequest {

    @NotBlank
    private String topic;

    @NotBlank
    private String targetRole;

    @NotBlank
    private String difficulty;
}
