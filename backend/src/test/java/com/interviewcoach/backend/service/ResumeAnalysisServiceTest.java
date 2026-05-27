package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiApiException;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.ResumeExtractionResult;
import com.interviewcoach.backend.model.Resume;
import com.interviewcoach.backend.model.ResumeAnalysis;
import com.interviewcoach.backend.repository.ResumeAnalysisRepository;
import com.interviewcoach.backend.repository.ResumeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumeAnalysisServiceTest {

    @Mock
    private ResumeRepository resumeRepository;

    @Mock
    private ResumeAnalysisRepository resumeAnalysisRepository;

    @Mock
    private GeminiClient geminiClient;

    @Mock
    private PromptBuilder promptBuilder;

    @Captor
    private ArgumentCaptor<ResumeAnalysis> analysisCaptor;

    private ResumeAnalysisService resumeAnalysisService;

    @BeforeEach
    void setUp() {
        resumeAnalysisService = new ResumeAnalysisService(
            resumeRepository,
            resumeAnalysisRepository,
            geminiClient,
            promptBuilder,
            new ObjectMapper()
        );
    }

    @Test
    void analyzeResume_successfulExtraction_savesResumeAnalysis() {
        Resume resume = Resume.builder()
            .id(10L)
            .userId(42L)
            .parsedText("Experienced software engineer with Java and Spring Boot.")
            .build();

        when(resumeRepository.findById(10L)).thenReturn(Optional.of(resume));
        when(promptBuilder.buildResumeExtractionPrompt(any())).thenReturn("PROMPT");

        String geminiJson = "{" +
            "\"skills\":[\"Java\",\"Spring Boot\"]," +
            "\"technologies\":[\"AWS\"]," +
            "\"frameworks\":[\"Spring\"]," +
            "\"databases\":[\"MySQL\"]," +
            "\"tools\":[\"Git\"]," +
            "\"projects\":[\"Built a microservice platform\"]," +
            "\"domains\":[\"FinTech\"]," +
            "\"strengths\":[\"Strong backend experience\"]," +
            "\"weaknesses\":[\"Limited frontend exposure\"]," +
            "\"recommendedRoles\":[\"Backend Engineer\"]," +
            "\"resumeScore\":85," +
            "\"confidenceScore\":0.92" +
            "}";
        when(geminiClient.callGemini("PROMPT")).thenReturn(geminiJson);
        when(resumeAnalysisRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ResumeExtractionResult result = resumeAnalysisService.analyzeResume(10L);

        assertThat(result).isNotNull();
        assertThat(result.getSkills()).containsExactly("Java", "Spring Boot");
        assertThat(result.getRecommendedRoles()).containsExactly("Backend Engineer");
        assertThat(result.getResumeScore()).isEqualTo(85);
        assertThat(result.getConfidenceScore()).isEqualTo(98.0);

        verify(resumeAnalysisRepository).save(analysisCaptor.capture());
        ResumeAnalysis saved = analysisCaptor.getValue();
        assertThat(saved.getResumeId()).isEqualTo(10L);
        assertThat(saved.getUserId()).isEqualTo(42L);
        assertThat(saved.getSkillsJson()).contains("Java");
        assertThat(saved.getRecommendedRolesJson()).contains("Backend Engineer");
        assertThat(saved.getResumeScore()).isEqualTo(85);
        assertThat(saved.getConfidenceScore()).isEqualTo(98.0);
        assertThat(saved.getAnalyzedAt()).isNotNull();
    }

    @Test
    void analyzeResume_malformedJson_returnsFallbackWithoutSaving() {
        Resume resume = Resume.builder()
            .id(11L)
            .userId(43L)
            .parsedText("Some resume text")
            .build();

        when(resumeRepository.findById(11L)).thenReturn(Optional.of(resume));
        when(promptBuilder.buildResumeExtractionPrompt(any())).thenReturn("PROMPT");
        when(geminiClient.callGemini("PROMPT")).thenReturn("{invalid-json");

        ResumeExtractionResult result = resumeAnalysisService.analyzeResume(11L);

        assertThat(result.getSkills()).isEmpty();
        assertThat(result.getResumeScore()).isEqualTo(0);
        assertThat(result.getConfidenceScore()).isEqualTo(0.0);

        verify(resumeAnalysisRepository, never()).save(any());
    }

    @Test
    void analyzeResume_emptyResponse_returnsFallbackWithoutSaving() {
        Resume resume = Resume.builder()
            .id(12L)
            .userId(44L)
            .parsedText("Another resume text")
            .build();

        when(resumeRepository.findById(12L)).thenReturn(Optional.of(resume));
        when(promptBuilder.buildResumeExtractionPrompt(any())).thenReturn("PROMPT");
        when(geminiClient.callGemini("PROMPT")).thenReturn("");

        ResumeExtractionResult result = resumeAnalysisService.analyzeResume(12L);

        assertThat(result.getSkills()).isEmpty();
        assertThat(result.getResumeScore()).isEqualTo(0);
        assertThat(result.getConfidenceScore()).isEqualTo(0.0);

        verify(resumeAnalysisRepository, never()).save(any());
    }

    @Test
    void analyzeResume_geminiFailure_returnsFallbackWithoutSaving() {
        Resume resume = Resume.builder()
            .id(13L)
            .userId(45L)
            .parsedText("Resume text for failure")
            .build();

        when(resumeRepository.findById(13L)).thenReturn(Optional.of(resume));
        when(promptBuilder.buildResumeExtractionPrompt(any())).thenReturn("PROMPT");
        when(geminiClient.callGemini("PROMPT")).thenThrow(new GeminiApiException("Quota exceeded", true, null));

        ResumeExtractionResult result = resumeAnalysisService.analyzeResume(13L);

        assertThat(result.getSkills()).isEmpty();
        assertThat(result.getResumeScore()).isEqualTo(0);
        assertThat(result.getConfidenceScore()).isEqualTo(0.0);

        verify(resumeAnalysisRepository, never()).save(any());
    }
}
