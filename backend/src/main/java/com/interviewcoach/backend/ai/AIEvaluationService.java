package com.interviewcoach.backend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiApiException;
import com.interviewcoach.backend.model.Question;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIEvaluationService {

    private final GeminiClient  geminiClient;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper  objectMapper = new ObjectMapper();

    public EvaluationResult evaluate(Question question, String candidateAnswer) {
        log.info("AI evaluation starting for question id={}", question.getId());
        try {
            String prompt      = promptBuilder.buildEvaluationPrompt(question, candidateAnswer);
            log.debug("Gemini prompt length={} for questionId={}", prompt.length(), question.getId());
            String rawResponse = geminiClient.callGemini(prompt);

            if (rawResponse == null || rawResponse.isBlank()) {
                log.warn("Gemini returned empty response for questionId={}", question.getId());
                return EvaluationResult.failed("AI feedback is temporarily unavailable. Please try again later.");
            }

            log.info("Gemini response received for questionId={}", question.getId());
            log.debug("Gemini raw response for questionId={}: {}", question.getId(), rawResponse);

            EvaluationResult result = objectMapper.readValue(
                cleanJson(rawResponse), EvaluationResult.class
            );
            result = clampAll(result);

            log.info("AI evaluation complete for questionId={} score={}", question.getId(), result.getOverallScore());
            return result;

        } catch (GeminiApiException e) {
            log.error("AI evaluation Gemini error for questionId={}: {}", question.getId(), e.getMessage(), e);
            if (e.isQuotaExceeded()) {
                return EvaluationResult.failed("AI feedback is temporarily unavailable because the Gemini free-tier quota has been exceeded. Please try again later.");
            }
            return EvaluationResult.failed("AI feedback is temporarily unavailable. Please try again later.");
        } catch (Exception e) {
            log.error("AI evaluation failed for questionId={}: {}", question.getId(), e.getMessage(), e);
            return EvaluationResult.failed("AI feedback is temporarily unavailable. Please try again later.");
        }
    }

    private String cleanJson(String raw) {
        String s = raw.trim();
        if (s.startsWith("```json")) s = s.substring(7);
        else if (s.startsWith("```"))   s = s.substring(3);
        if (s.endsWith("```"))          s = s.substring(0, s.length() - 3);
        return s.trim();
    }

    private EvaluationResult clampAll(EvaluationResult r) {
        return EvaluationResult.builder()
            .success(true)
            .overallScore(       clamp(r.getOverallScore()))
            .technicalAccuracy(  clamp(r.getTechnicalAccuracy()))
            .communication(      clamp(r.getCommunication()))
            .problemSolving(     clamp(r.getProblemSolving()))
            .confidence(         clamp(r.getConfidence()))
            .strengths(          safe(r.getStrengths()))
            .improvements(       safe(r.getImprovements()))
            .missingConcepts(    safe(r.getMissingConcepts()))
            .modelAnswer(        r.getModelAnswer()          != null ? r.getModelAnswer()          : "")
            .interviewerFeedback(r.getInterviewerFeedback()  != null ? r.getInterviewerFeedback()  : "")
            .build();
    }

    private int          clamp(int v)          { return Math.max(0, Math.min(100, v)); }
    private List<String> safe(List<String> l)  { return l != null ? l : List.of();    }
}
