package com.interviewcoach.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitCodingSolutionRequest {
    @NotBlank(message = "Code submission cannot be empty")
    private String code;
}
