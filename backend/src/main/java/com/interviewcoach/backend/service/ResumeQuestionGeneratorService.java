package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.ResumeExtractionResult;
import com.interviewcoach.backend.dto.ResumeQuestionRequest;
import com.interviewcoach.backend.dto.ResumeQuestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeQuestionGeneratorService {

    private final ResumeAnalysisService resumeAnalysisService;
    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ResumeQuestionResponse generateQuestions(Long resumeId, ResumeQuestionRequest request) {
        String difficulty = normalizeDifficulty(request.getDifficulty());
        int count = request.getCount() == null || request.getCount() < 1 ? 5 : request.getCount();

        ResumeExtractionResult analysis = resumeAnalysisService.getLatestAnalysis(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("No resume analysis found for resume: " + resumeId));

        if ((analysis.getSkills() == null || analysis.getSkills().isEmpty())
            && (analysis.getProjects() == null || analysis.getProjects().isEmpty())
            && (analysis.getTechnologies() == null || analysis.getTechnologies().isEmpty())) {
            log.warn("Resume {} analysis contains no skills, projects, or technologies", resumeId);
            return buildFallbackResponse();
        }

        String prompt = promptBuilder.buildResumeQuestionPrompt(analysis, difficulty, count);
        String geminiResponse;
        try {
            geminiResponse = geminiClient.callGemini(prompt);
        } catch (Exception e) {
            log.error("Gemini question generation failed for resume {}", resumeId, e);
            return buildFallbackResponse();
        }

        if (geminiResponse == null || geminiResponse.isBlank()) {
            log.warn("Gemini returned empty question generation response for resume {}", resumeId);
            return buildFallbackResponse();
        }

        ResumeQuestionResponse response = parseGeminiResponse(geminiResponse);
        if (response == null || response.getQuestions() == null || response.getQuestions().isEmpty()) {
            log.warn("Failed to parse generated questions for resume {}", resumeId);
            return buildFallbackResponse();
        }

        return response;
    }

    private ResumeQuestionResponse parseGeminiResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode questionsNode = root.path("questions");
            List<String> validQuestions = new ArrayList<>();

            if (questionsNode.isArray()) {
                for (JsonNode node : questionsNode) {
                    if (node.isTextual()) {
                        String text = node.asText().trim();
                        if (!text.isBlank()) {
                            validQuestions.add(text);
                        }
                    }
                }
            } else if (root.isArray()) {
                for (JsonNode node : root) {
                    if (node.isTextual()) {
                        String text = node.asText().trim();
                        if (!text.isBlank()) {
                            validQuestions.add(text);
                        }
                    }
                }
            }

            if (validQuestions.isEmpty()) {
                return null;
            }

            return ResumeQuestionResponse.builder()
                .questions(validQuestions)
                .build();
        } catch (Exception e) {
            log.error("Could not parse Gemini question response", e);
            return null;
        }
    }

    private String normalizeDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isBlank()) {
            return "MEDIUM";
        }
        String normalized = difficulty.trim().toUpperCase();
        return switch (normalized) {
            case "EASY", "MEDIUM", "HARD" -> normalized;
            default -> "MEDIUM";
        };
    }

    private ResumeQuestionResponse buildFallbackResponse() {
        return ResumeQuestionResponse.builder().questions(List.of()).build();
    }
}
