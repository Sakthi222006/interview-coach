package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.ResumeAnalysisHistoryResponse;
import com.interviewcoach.backend.dto.ResumeExtractionResult;
import com.interviewcoach.backend.dto.ResumeMatchRequest;
import com.interviewcoach.backend.dto.ResumeMatchResponse;
import com.interviewcoach.backend.dto.ResumeReadinessResponse;
import com.interviewcoach.backend.dto.ResumeUploadResponse;
import com.interviewcoach.backend.model.Resume;
import com.interviewcoach.backend.model.ResumeAnalysis;
import com.interviewcoach.backend.repository.ResumeAnalysisRepository;
import com.interviewcoach.backend.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeIntelligenceService {

    private final ResumeParserService resumeParserService;
    private final ResumeAnalysisService resumeAnalysisService;
    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final GeminiClient geminiClient;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;

    public ResumeUploadResponse uploadResume(MultipartFile file, Long userId) {
        var preview = resumeParserService.parseAndStoreResume(file, userId);
        return ResumeUploadResponse.builder()
            .resumeId(preview.resumeId())
            .filename(preview.filename())
            .previewText(preview.parsedTextPreview())
            .uploadedAt(preview.uploadedAt())
            .build();
    }

    public ResumeExtractionResult analyzeResume(Long resumeId, Long userId) {
        Resume resume = findResumeForUser(resumeId, userId);
        return resumeAnalysisService.analyzeResume(resume.getId());
    }

    public Optional<ResumeExtractionResult> getLatestAnalysis(Long resumeId, Long userId) {
        validateResumeOwnership(resumeId, userId);
        return resumeAnalysisService.getLatestAnalysis(resumeId);
    }

    public ResumeAnalysisHistoryResponse getAnalysisHistory(Long resumeId, Long userId) {
        validateResumeOwnership(resumeId, userId);
        List<ResumeAnalysis> history = resumeAnalysisRepository.findByResumeIdOrderByAnalyzedAtDesc(resumeId);
        if (history.isEmpty()) {
            return ResumeAnalysisHistoryResponse.builder().history(Collections.emptyList()).build();
        }
        List<ResumeAnalysisHistoryResponse.ResumeAnalysisHistoryEntry> entries = new ArrayList<>();
        for (ResumeAnalysis analysis : history) {
            try {
                ResumeExtractionResult result = objectMapper.readValue(analysis.getGeminiRawResponse(), ResumeExtractionResult.class);
                entries.add(ResumeAnalysisHistoryResponse.ResumeAnalysisHistoryEntry.builder()
                    .analyzedAt(analysis.getAnalyzedAt())
                    .analysis(result)
                    .build());
            } catch (Exception e) {
                log.warn("Failed to deserialize resume analysis history entry {}", analysis.getId(), e);
            }
        }
        return ResumeAnalysisHistoryResponse.builder().history(entries).build();
    }

    public ResumeMatchResponse matchResume(Long resumeId, ResumeMatchRequest request, Long userId) {
        Resume resume = findResumeForUser(resumeId, userId);
        ResumeExtractionResult analysis = resumeAnalysisService.getLatestAnalysis(resume.getId())
            .orElseThrow(() -> new IllegalArgumentException("No analysis found for resume " + resumeId));

        try {
            String prompt = promptBuilder.buildResumeMatchPrompt(
                analysis,
                request.getTargetRole(),
                request.getJobDescription(),
                request.getDesiredSkills()
            );
            String response = geminiClient.callGemini(prompt);
            if (response == null || response.isBlank()) {
                log.warn("Gemini returned empty resume job match response for resume {}", resumeId);
                return buildFallbackMatch(analysis, request);
            }
            ResumeMatchResponse matchResponse = objectMapper.readValue(response, ResumeMatchResponse.class);
            return sanitizeMatchResponse(matchResponse, analysis, request);
        } catch (Exception e) {
            log.error("Failed to generate resume match for resume {}", resumeId, e);
            return buildFallbackMatch(analysis, request);
        }
    }

    public ResumeReadinessResponse getReadiness(Long resumeId, Long userId, String targetRole) {
        Resume resume = findResumeForUser(resumeId, userId);
        ResumeExtractionResult analysis = resumeAnalysisService.getLatestAnalysis(resume.getId())
            .orElseThrow(() -> new IllegalArgumentException("No analysis found for resume " + resumeId));

        try {
            String prompt = promptBuilder.buildResumeReadinessPrompt(analysis, targetRole);
            String response = geminiClient.callGemini(prompt);
            if (response == null || response.isBlank()) {
                log.warn("Gemini returned empty readiness response for resume {}", resumeId);
                return buildFallbackReadiness(analysis);
            }
            ResumeReadinessResponse readiness = objectMapper.readValue(response, ResumeReadinessResponse.class);
            return sanitizeReadinessResponse(readiness, analysis);
        } catch (Exception e) {
            log.error("Failed to compute readiness for resume {}", resumeId, e);
            return buildFallbackReadiness(analysis);
        }
    }

    private ResumeMatchResponse sanitizeMatchResponse(ResumeMatchResponse response, ResumeExtractionResult analysis, ResumeMatchRequest request) {
        if (response == null) {
            return buildFallbackMatch(analysis, request);
        }
        if (response.getMatchedRole() == null || response.getMatchedRole().isBlank()) {
            response.setMatchedRole(request.getTargetRole() != null && !request.getTargetRole().isBlank()
                ? request.getTargetRole()
                : analysis.getRecommendedRoles() != null && !analysis.getRecommendedRoles().isEmpty()
                    ? analysis.getRecommendedRoles().get(0)
                    : "Candidate Role"
            );
        }
        if (response.getMatchScore() == null) {
            response.setMatchScore(buildFallbackMatch(analysis, request).getMatchScore());
        }
        if (response.getRecommendedSkills() == null) {
            response.setRecommendedSkills(analysis.getSkills() != null ? analysis.getSkills() : Collections.emptyList());
        }
        if (response.getMatchHighlights() == null) {
            response.setMatchHighlights(List.of("Skills and projects were considered for match generation."));
        }
        if (response.getSuggestedGaps() == null) {
            response.setSuggestedGaps(List.of("Review role requirements and build related projects to close gaps."));
        }
        return response;
    }

    private ResumeReadinessResponse sanitizeReadinessResponse(ResumeReadinessResponse response, ResumeExtractionResult analysis) {
        if (response == null) {
            return buildFallbackReadiness(analysis);
        }
        if (response.getOverallReadiness() == null || response.getOverallReadiness().isBlank()) {
            response.setOverallReadiness(deriveReadinessLevel(analysis));
        }
        if (response.getReadinessScore() == null) {
            response.setReadinessScore(deriveReadinessScore(analysis));
        }
        if (response.getKeyStrengths() == null) {
            response.setKeyStrengths(analysis.getStrengths() != null ? analysis.getStrengths() : Collections.emptyList());
        }
        if (response.getImprovementAreas() == null) {
            response.setImprovementAreas(List.of("Clarify career goals and fill identified skill gaps."));
        }
        if (response.getNextSteps() == null) {
            response.setNextSteps(List.of("Use the roadmap generator to create a plan based on resume strengths and gaps."));
        }
        return response;
    }

    private ResumeMatchResponse buildFallbackMatch(ResumeExtractionResult analysis, ResumeMatchRequest request) {
        int score = deriveReadinessScore(analysis);
        List<String> recommendedSkills = analysis.getSkills() != null ? analysis.getSkills() : Collections.emptyList();
        return ResumeMatchResponse.builder()
            .matchedRole(request.getTargetRole() != null && !request.getTargetRole().isBlank() ? request.getTargetRole() :
                analysis.getRecommendedRoles() != null && !analysis.getRecommendedRoles().isEmpty() ? analysis.getRecommendedRoles().get(0) : "Candidate Role")
            .matchScore(Math.min(100, score))
            .roleFitSummary("Candidate has a solid foundational match based on extracted skills and project experience.")
            .recommendedSkills(recommendedSkills)
            .matchHighlights(List.of("Skills, projects, and technologies were used to estimate a fit."))
            .suggestedGaps(List.of("Consider aligning skills more closely to the target role and building role-specific examples."))
            .build();
    }

    private ResumeReadinessResponse buildFallbackReadiness(ResumeExtractionResult analysis) {
        String overall = deriveReadinessLevel(analysis);
        int score = deriveReadinessScore(analysis);
        return ResumeReadinessResponse.builder()
            .overallReadiness(overall)
            .readinessScore(Math.min(100, Math.max(0, score)))
            .keyStrengths(analysis.getStrengths() != null ? analysis.getStrengths() : Collections.emptyList())
            .improvementAreas(List.of("Clarify the target role, strengthen weak topics, and add measurable project results."))
            .nextSteps(List.of("Review resume analysis, generate a roadmap, and practice targeted interview questions."))
            .build();
    }

    private int deriveReadinessScore(ResumeExtractionResult analysis) {
        if (analysis == null) {
            return 0;
        }
        int baseScore = analysis.getResumeScore() != null ? analysis.getResumeScore() : 0;
        double confidence = analysis.getConfidenceScore() != null ? analysis.getConfidenceScore() : 0.0;
        int skillBonus = analysis.getSkills() != null ? Math.min(10, analysis.getSkills().size()) * 2 : 0;
        int projectBonus = analysis.getProjects() != null ? Math.min(5, analysis.getProjects().size()) * 3 : 0;
        int calculated = (int) Math.round(baseScore * 0.7 + confidence * 30 + skillBonus + projectBonus);
        return Math.max(0, Math.min(100, calculated));
    }

    private String deriveReadinessLevel(ResumeExtractionResult analysis) {
        int score = deriveReadinessScore(analysis);
        if (score >= 75) return "HIGH";
        if (score >= 50) return "MEDIUM";
        return "LOW";
    }

    private void validateResumeOwnership(Long resumeId, Long userId) {
        if (!resumeRepository.findByIdAndUserId(resumeId, userId).isPresent()) {
            throw new IllegalArgumentException("Resume not found for user: " + resumeId);
        }
    }

    private Resume findResumeForUser(Long resumeId, Long userId) {
        return resumeRepository.findByIdAndUserId(resumeId, userId)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found for user: " + resumeId));
    }
}
