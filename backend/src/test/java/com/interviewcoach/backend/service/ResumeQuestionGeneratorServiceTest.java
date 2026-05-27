package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.ResumeExtractionResult;
import com.interviewcoach.backend.dto.ResumeQuestionRequest;
import com.interviewcoach.backend.dto.ResumeQuestionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeQuestionGeneratorServiceTest {

    @Mock
    private ResumeAnalysisService resumeAnalysisService;

    @Mock
    private GeminiClient geminiClient;

    @Mock
    private PromptBuilder promptBuilder;

    private ResumeQuestionGeneratorService subject;

    @BeforeEach
    void setUp() {
        subject = new ResumeQuestionGeneratorService(
            resumeAnalysisService,
            geminiClient,
            promptBuilder,
            new ObjectMapper()
        );
    }

    @Test
    void generateQuestions_returnsParsedQuestionList() {
        ResumeExtractionResult analysis = ResumeExtractionResult.builder()
            .skills(List.of("Java", "Spring Boot"))
            .projects(List.of("Built a REST microservice"))
            .technologies(List.of("MySQL"))
            .build();

        when(resumeAnalysisService.getLatestAnalysis(20L)).thenReturn(Optional.of(analysis));
        when(promptBuilder.buildResumeQuestionPrompt(any(), anyString(), anyInt())).thenReturn("PROMPT");
        when(geminiClient.callGemini("PROMPT")).thenReturn("{\"questions\":[\"Explain how you designed the microservice.\",\"Describe a time you fixed a production bug.\"]}");

        ResumeQuestionResponse response = subject.generateQuestions(20L, new ResumeQuestionRequest("HARD", 2));

        assertThat(response).isNotNull();
        assertThat(response.getQuestions()).containsExactly(
            "Explain how you designed the microservice.",
            "Describe a time you fixed a production bug."
        );
    }

    @Test
    void generateQuestions_invalidGeminiJson_returnsEmptyQuestionList() {
        ResumeExtractionResult analysis = ResumeExtractionResult.builder()
            .skills(List.of("Java"))
            .projects(List.of("Built a REST API"))
            .build();

        when(resumeAnalysisService.getLatestAnalysis(21L)).thenReturn(Optional.of(analysis));
        when(promptBuilder.buildResumeQuestionPrompt(any(), anyString(), anyInt())).thenReturn("PROMPT");
        when(geminiClient.callGemini("PROMPT")).thenReturn("{invalid-json");

        ResumeQuestionResponse response = subject.generateQuestions(21L, new ResumeQuestionRequest("MEDIUM", 3));

        assertThat(response).isNotNull();
        assertThat(response.getQuestions()).isEmpty();
    }

    @Test
    void generateQuestions_blankGeminiResponse_returnsEmptyQuestionList() {
        ResumeExtractionResult analysis = ResumeExtractionResult.builder()
            .skills(List.of("Python"))
            .projects(List.of("Created a data pipeline"))
            .build();

        when(resumeAnalysisService.getLatestAnalysis(22L)).thenReturn(Optional.of(analysis));
        when(promptBuilder.buildResumeQuestionPrompt(any(), anyString(), anyInt())).thenReturn("PROMPT");
        when(geminiClient.callGemini("PROMPT")).thenReturn("");

        ResumeQuestionResponse response = subject.generateQuestions(22L, new ResumeQuestionRequest("EASY", 4));

        assertThat(response).isNotNull();
        assertThat(response.getQuestions()).isEmpty();
    }

    @Test
    void generateQuestions_noLatestAnalysis_throwsIllegalArgumentException() {
        when(resumeAnalysisService.getLatestAnalysis(23L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> subject.generateQuestions(23L, new ResumeQuestionRequest("MEDIUM", 3)));
    }
}
