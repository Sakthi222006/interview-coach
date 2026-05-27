package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.RoadmapRequest;
import com.interviewcoach.backend.dto.RoadmapResponse;
import com.interviewcoach.backend.dto.ResumeExtractionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CareerRoadmapServiceTest {

    private ResumeAnalysisService resumeAnalysisService;
    private PerformanceAnalyticsService analyticsService;
    private GeminiClient geminiClient;
    private PromptBuilder promptBuilder;
    private ObjectMapper objectMapper;
    private CareerRoadmapService service;

    @BeforeEach
    void setup() {
        resumeAnalysisService = Mockito.mock(ResumeAnalysisService.class);
        analyticsService = Mockito.mock(PerformanceAnalyticsService.class);
        geminiClient = Mockito.mock(GeminiClient.class);
        promptBuilder = new PromptBuilder();
        objectMapper = new ObjectMapper();

        service = new CareerRoadmapService(resumeAnalysisService, analyticsService, geminiClient, promptBuilder, objectMapper);
    }

    @Test
    void generateRoadmap_success_parsesResponse() throws Exception {
        ResumeExtractionResult result = ResumeExtractionResult.builder()
            .skills(List.of("Java", "Spring"))
            .build();

        when(resumeAnalysisService.getLatestAnalysis(10L)).thenReturn(Optional.of(result));

        String json = "{\"overallReadiness\":\"MEDIUM\",\"readinessScore\":65,\"phases\":[] }";
        when(geminiClient.callGemini(anyString())).thenReturn(json);

        var req = RoadmapRequest.builder().userId(1L).resumeId(10L).targetRole("Backend Engineer").build();
        var opt = service.generateRoadmap(req);
        assertTrue(opt.isPresent());
        RoadmapResponse resp = opt.get();
        assertEquals("MEDIUM", resp.getOverallReadiness());
        assertEquals(65, resp.getReadinessScore());
    }
}
