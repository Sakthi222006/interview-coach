package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.ResumeExtractionResult;
import com.interviewcoach.backend.model.Resume;
import com.interviewcoach.backend.model.ResumeAnalysis;
import com.interviewcoach.backend.repository.ResumeAnalysisRepository;
import com.interviewcoach.backend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ResumeExtractionResult analyzeResume(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found: " + resumeId));

        try {
            String extractedText = resume.getParsedText();
            if (extractedText == null || extractedText.isBlank()) {
                log.warn("Resume {} has no parsed text", resumeId);
                return buildFallbackResult("Resume text could not be extracted");
            }

            String prompt = promptBuilder.buildResumeExtractionPrompt(extractedText);
            String geminiResponse = geminiClient.callGemini(prompt);

            if (geminiResponse == null || geminiResponse.isBlank()) {
                log.warn("Gemini returned empty response for resume {}", resumeId);
                return buildFallbackResult("Gemini analysis failed");
            }

            ResumeExtractionResult result = parseGeminiResponse(geminiResponse);
            if (result == null) {
                log.warn("Failed to parse Gemini JSON response for resume {}", resumeId);
                return buildFallbackResult("Failed to parse analysis response");
            }

            // Calculate confidence score based on extraction completeness
            double confidenceScore = calculateConfidenceScore(result);
            result.setConfidenceScore(confidenceScore);

            // Persist the analysis
            saveResumeAnalysis(resume, result);

            log.info("Successfully analyzed resume {} with score={} confidence={}", resumeId, result.getResumeScore(), confidenceScore);
            return result;

        } catch (Exception e) {
            log.error("Error analyzing resume {}", resumeId, e);
            return buildFallbackResult("Analysis error: " + e.getMessage());
        }
    }

    private ResumeExtractionResult parseGeminiResponse(String jsonString) {
        try {
            var root = objectMapper.readTree(jsonString);
            return ResumeExtractionResult.builder()
                .skills(parseStringList(root.path("skills")))
                .technologies(parseStringList(root.path("technologies")))
                .frameworks(parseStringList(root.path("frameworks")))
                .databases(parseStringList(root.path("databases")))
                .tools(parseStringList(root.path("tools")))
                .projects(parseStringList(root.path("projects")))
                .domains(parseStringList(root.path("domains")))
                .strengths(parseStringList(root.path("strengths")))
                .weaknesses(parseStringList(root.path("weaknesses")))
                .recommendedRoles(parseStringList(root.path("recommendedRoles")))
                .resumeScore(parseInteger(root.path("resumeScore"), 0))
                .confidenceScore(parseDouble(root.path("confidenceScore"), 0.0))
                .build();
        } catch (Exception e) {
            log.error("Failed to parse JSON response: {}", jsonString, e);
            return null;
        }
    }

    private List<String> parseStringList(com.fasterxml.jackson.databind.JsonNode node) {
        if (node == null || node.isNull()) {
            return List.of();
        }
        if (node.isArray()) {
            return objectMapper.convertValue(node, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        }
        if (node.isTextual()) {
            String value = node.asText().trim();
            return value.isBlank() ? List.of() : List.of(value);
        }
        return List.of();
    }

    private int parseInteger(com.fasterxml.jackson.databind.JsonNode node, int defaultValue) {
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        if (node.isInt() || node.isLong() || node.isNumber()) {
            return Math.max(0, Math.min(100, node.asInt(defaultValue)));
        }
        if (node.isTextual()) {
            try {
                return Math.max(0, Math.min(100, Integer.parseInt(node.asText().trim())));
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private double parseDouble(com.fasterxml.jackson.databind.JsonNode node, double defaultValue) {
        if (node == null || node.isNull()) {
            return defaultValue;
        }
        if (node.isNumber()) {
            double value = node.asDouble(defaultValue);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                return defaultValue;
            }
            return Math.max(0.0, Math.min(1.0, value));
        }
        if (node.isTextual()) {
            try {
                double value = Double.parseDouble(node.asText().trim());
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    return defaultValue;
                }
                return Math.max(0.0, Math.min(1.0, value));
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private double calculateConfidenceScore(ResumeExtractionResult result) {
        if (result == null) {
            return 0.0;
        }

        int filledSections = 0;
        int totalSections = 11; // count the extraction fields that should be present

        if (result.getSkills() != null && !result.getSkills().isEmpty()) filledSections++;
        if (result.getTechnologies() != null && !result.getTechnologies().isEmpty()) filledSections++;
        if (result.getFrameworks() != null && !result.getFrameworks().isEmpty()) filledSections++;
        if (result.getDatabases() != null && !result.getDatabases().isEmpty()) filledSections++;
        if (result.getTools() != null && !result.getTools().isEmpty()) filledSections++;
        if (result.getProjects() != null && !result.getProjects().isEmpty()) filledSections++;
        if (result.getDomains() != null && !result.getDomains().isEmpty()) filledSections++;
        if (result.getStrengths() != null && !result.getStrengths().isEmpty()) filledSections++;
        if (result.getWeaknesses() != null && !result.getWeaknesses().isEmpty()) filledSections++;
        if (result.getRecommendedRoles() != null && !result.getRecommendedRoles().isEmpty()) filledSections++;
        if (result.getResumeScore() != null && result.getResumeScore() > 0) filledSections++;

        double coverageRatio = (double) filledSections / totalSections;
        double rawGeminiConfidence = result.getConfidenceScore() != null ? result.getConfidenceScore() : 0.0;

        double weightedScore = coverageRatio * 0.7 + rawGeminiConfidence * 0.3;
        return Math.round(Math.max(0.0, Math.min(1.0, weightedScore)) * 100.0);
    }

    private void saveResumeAnalysis(Resume resume, ResumeExtractionResult result) {
        try {
            ResumeAnalysis analysis = ResumeAnalysis.builder()
                .resumeId(resume.getId())
                .userId(resume.getUserId())
                .skillsJson(objectMapper.writeValueAsString(result.getSkills()))
                .technologiesJson(objectMapper.writeValueAsString(result.getTechnologies()))
                .frameworksJson(objectMapper.writeValueAsString(result.getFrameworks()))
                .databasesJson(objectMapper.writeValueAsString(result.getDatabases()))
                .toolsJson(objectMapper.writeValueAsString(result.getTools()))
                .projectsJson(objectMapper.writeValueAsString(result.getProjects()))
                .domainsJson(objectMapper.writeValueAsString(result.getDomains()))
                .strengthsJson(objectMapper.writeValueAsString(result.getStrengths()))
                .weaknessesJson(objectMapper.writeValueAsString(result.getWeaknesses()))
                .recommendedRolesJson(objectMapper.writeValueAsString(result.getRecommendedRoles()))
                .resumeScore(result.getResumeScore() != null ? result.getResumeScore() : 0)
                .confidenceScore(result.getConfidenceScore() != null ? result.getConfidenceScore() : 0.0)
                .geminiRawResponse(objectMapper.writeValueAsString(result))
                .analyzedAt(LocalDateTime.now())
                .build();

            resumeAnalysisRepository.save(analysis);
            log.info("Saved ResumeAnalysis for resume {}", resume.getId());
        } catch (Exception e) {
            log.error("Failed to save ResumeAnalysis for resume {}", resume.getId(), e);
            throw new IllegalStateException("Could not persist resume analysis", e);
        }
    }

    private ResumeExtractionResult buildFallbackResult(String reason) {
        log.warn("Building fallback analysis result: {}", reason);
        return ResumeExtractionResult.builder()
            .skills(List.of())
            .technologies(List.of())
            .frameworks(List.of())
            .databases(List.of())
            .tools(List.of())
            .projects(List.of())
            .domains(List.of())
            .strengths(List.of())
            .weaknesses(List.of())
            .recommendedRoles(List.of())
            .resumeScore(0)
            .confidenceScore(0.0)
            .build();
    }

    public Optional<ResumeExtractionResult> getLatestAnalysis(Long resumeId) {
        return resumeAnalysisRepository.findTopByResumeIdOrderByAnalyzedAtDesc(resumeId)
            .map(analysis -> {
                try {
                    return objectMapper.readValue(analysis.getGeminiRawResponse(), ResumeExtractionResult.class);
                } catch (Exception e) {
                    log.error("Failed to deserialize analysis for resume {}", resumeId, e);
                    return null;
                }
            });
    }
}
