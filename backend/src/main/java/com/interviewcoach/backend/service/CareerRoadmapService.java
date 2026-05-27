package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.RoadmapRequest;
import com.interviewcoach.backend.dto.RoadmapResponse;
import com.interviewcoach.backend.dto.ResumeExtractionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CareerRoadmapService {

    private final ResumeAnalysisService resumeAnalysisService;
    private final PerformanceAnalyticsService analyticsService;
    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public Optional<RoadmapResponse> generateRoadmap(RoadmapRequest request) {
        try {
            ResumeExtractionResult analysis = resumeAnalysisService.getLatestAnalysis(request.getResumeId()).orElse(null);

            String analyticsSummary = "";
            try {
                var topicAnalysis = analyticsService.getTopicAnalysis(request.getUserId());
                analyticsSummary = "weakTopics=" + String.join(",", topicAnalysis.getWeakTopics());
            } catch (Exception ignored) {}

            var prompt = promptBuilder.buildCareerRoadmapPrompt(
                analysis != null ? analysis.getSkills() : java.util.List.of(),
                request.getMissingConcepts(),
                request.getWeakTopics(),
                request.getAnalyticsSummary() != null ? request.getAnalyticsSummary() : analyticsSummary,
                request.getTargetRole()
            );

            String geminiResponse = geminiClient.callGemini(prompt);
            if (geminiResponse == null || geminiResponse.isBlank()) {
                log.warn("Gemini returned empty roadmap response");
                return Optional.empty();
            }

            RoadmapResponse roadmap = objectMapper.readValue(geminiResponse, RoadmapResponse.class);
            return Optional.ofNullable(roadmap);

        } catch (Exception e) {
            log.error("Failed to generate roadmap", e);
            return Optional.empty();
        }
    }
}
