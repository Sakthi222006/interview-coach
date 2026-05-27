package com.interviewcoach.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoiceTranscriptRequest {

    private String questionText;

    @NotBlank
    private String transcript;

    @NotNull
    private Integer durationSeconds;
}
