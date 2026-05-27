package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.GeminiApiException;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.*;
import com.interviewcoach.backend.model.*;
import com.interviewcoach.backend.repository.VoiceAnswerRepository;
import com.interviewcoach.backend.repository.VoiceInterviewResultRepository;
import com.interviewcoach.backend.repository.VoiceInterviewSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceInterviewService {

    private final VoiceInterviewSessionRepository sessionRepository;
    private final VoiceAnswerRepository answerRepository;
    private final VoiceInterviewResultRepository resultRepository;
    private final SpeechAnalysisService speechAnalysisService;
    private final ConversationMemoryService conversationMemoryService;
    private final PromptBuilder promptBuilder;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public VoiceSessionResponse startSession(VoiceInterviewStartRequest request, Long userId) {
        VoiceInterviewSession session = VoiceInterviewSession.builder()
            .user(User.builder().id(userId).build())
            .topic(request.getTopic())
            .targetRole(request.getTargetRole())
            .difficulty(request.getDifficulty())
            .status("IN_PROGRESS")
            .build();

        session = sessionRepository.save(session);
        return toSessionResponse(session);
    }

    @Transactional
    public VoiceAnswerResponse submitTranscript(Long sessionId, VoiceTranscriptRequest request, Long userId) {
        VoiceInterviewSession session = findSession(sessionId, userId);
        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw new IllegalArgumentException("Voice interview session is not active");
        }

        SpeechAnalysisResult analysis = speechAnalysisService.analyze(request.getTranscript(), request.getDurationSeconds());
        VoiceEvaluationResult evaluation = evaluateTranscript(session, request.getQuestionText(), request.getTranscript());

        VoiceAnswer answer = VoiceAnswer.builder()
            .session(session)
            .transcript(request.getTranscript())
            .wordCount(analysis.getWordCount())
            .fillerWordCount(analysis.getFillerWordCount())
            .speakingRateWpm(analysis.getSpeakingRateWpm())
            .confidenceScore(evaluation.getConfidence())
            .communicationScore(evaluation.getCommunication())
            .clarityScore(evaluation.getClarity())
            .grammarScore(evaluation.getGrammar())
            .technicalScore(evaluation.getTechnicalQuality())
            .starScore(evaluation.getStarScore())
            .completenessScore(evaluation.getCompleteness())
            .overallScore(evaluation.getOverallScore())
            .modelAnswer(evaluation.getModelAnswer())
            .interviewerFeedback(evaluation.getInterviewerFeedback())
            .strengthsJson(toJson(evaluation.getStrengths()))
            .improvementsJson(toJson(evaluation.getImprovements()))
            .missingPointsJson(toJson(evaluation.getMissingPoints()))
            .nextQuestion(evaluation.getNextQuestion())
            .build();

        session.getAnswers().add(answer);
        conversationMemoryService.addMemory(session, request.getTranscript());
        updateSessionResult(session);
        sessionRepository.save(session);
        answer = answerRepository.save(answer);

        return toAnswerResponse(answer);
    }

    @Transactional
    public VoiceSessionResponse stopSession(Long sessionId, Long userId) {
        VoiceInterviewSession session = findSession(sessionId, userId);
        session.setStatus("COMPLETED");
        session.setCompletedAt(LocalDateTime.now());
        if (session.getStartedAt() != null) {
            session.setDurationSeconds((int) Math.max(0, java.time.Duration.between(session.getStartedAt(), session.getCompletedAt()).getSeconds()));
        }
        updateSessionResult(session);
        sessionRepository.save(session);
        return toSessionResponse(session);
    }

    @Transactional(readOnly = true)
    public VoiceSessionSummaryResponse getSessionSummary(Long sessionId, Long userId) {
        VoiceInterviewSession session = findSession(sessionId, userId);
        return toSummaryResponse(session);
    }

    @Transactional(readOnly = true)
    public VoiceSessionResponse getSession(Long sessionId, Long userId) {
        VoiceInterviewSession session = findSession(sessionId, userId);
        return toSessionResponse(session);
    }

    private VoiceInterviewSession findSession(Long sessionId, Long userId) {
        return sessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new AccessDeniedException("Voice interview session not found or access denied"));
    }

    private VoiceEvaluationResult evaluateTranscript(VoiceInterviewSession session, String questionText, String transcript) {
        try {
            String prompt = promptBuilder.buildVoiceInterviewPrompt(
                session.getTopic(),
                session.getTargetRole(),
                session.getDifficulty(),
                questionText,
                transcript,
                conversationMemoryService.getConversationHistory(session)
            );
            String rawResponse = geminiClient.callGemini(prompt);
            if (rawResponse == null || rawResponse.isBlank()) {
                throw new RuntimeException("AI feedback unavailable");
            }
            return objectMapper.readValue(cleanJson(rawResponse), VoiceEvaluationResult.class);
        } catch (GeminiApiException e) {
            log.error("Gemini evaluation failed for voice transcript", e);
            throw new RuntimeException("AI evaluation unavailable. Please try again later.");
        } catch (Exception e) {
            log.error("Unable to parse voice evaluation response", e);
            throw new RuntimeException("Failed to evaluate transcript");
        }
    }

    private void updateSessionResult(VoiceInterviewSession session) {
        if (session.getAnswers().isEmpty()) {
            return;
        }

        List<VoiceAnswer> answers = session.getAnswers();
        double averageScore = answers.stream().mapToInt(a -> a.getOverallScore() != null ? a.getOverallScore() : 0).average().orElse(0.0);
        double averageConfidence = answers.stream().mapToInt(a -> a.getConfidenceScore() != null ? a.getConfidenceScore() : 0).average().orElse(0.0);
        double averageCommunication = answers.stream().mapToInt(a -> a.getCommunicationScore() != null ? a.getCommunicationScore() : 0).average().orElse(0.0);
        double averageClarity = answers.stream().mapToInt(a -> a.getClarityScore() != null ? a.getClarityScore() : 0).average().orElse(0.0);
        double averageGrammar = answers.stream().mapToInt(a -> a.getGrammarScore() != null ? a.getGrammarScore() : 0).average().orElse(0.0);
        double averageTechnical = answers.stream().mapToInt(a -> a.getTechnicalScore() != null ? a.getTechnicalScore() : 0).average().orElse(0.0);
        double averageStar = answers.stream().mapToInt(a -> a.getStarScore() != null ? a.getStarScore() : 0).average().orElse(0.0);
        double averageCompleteness = answers.stream().mapToInt(a -> a.getCompletenessScore() != null ? a.getCompletenessScore() : 0).average().orElse(0.0);
        double averageWpm = answers.stream().mapToDouble(a -> a.getSpeakingRateWpm() != null ? a.getSpeakingRateWpm() : 0.0).average().orElse(0.0);

        VoiceInterviewResult result = session.getResult();
        if (result == null) {
            result = VoiceInterviewResult.builder()
                .session(session)
                .build();
        }

        result.setAverageScore(averageScore);
        result.setAverageConfidence(averageConfidence);
        result.setAverageCommunication(averageCommunication);
        result.setAverageClarity(averageClarity);
        result.setAverageGrammar(averageGrammar);
        result.setAverageTechnical(averageTechnical);
        result.setAverageStar(averageStar);
        result.setAverageCompleteness(averageCompleteness);
        result.setAverageWpm(averageWpm);
        result.setConfidenceTrendJson(toJson(answers.stream().map(a -> a.getConfidenceScore() == null ? 0 : a.getConfidenceScore()).toList()));
        result.setCommunicationTrendJson(toJson(answers.stream().map(a -> a.getCommunicationScore() == null ? 0 : a.getCommunicationScore()).toList()));
        result.setImprovementHistoryJson(toJson(answers.stream().flatMap(a -> {
            if (a.getImprovementsJson() == null) {
                return java.util.stream.Stream.empty();
            }
            try {
                List<String> improvements = objectMapper.readValue(a.getImprovementsJson(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                return improvements.stream();
            } catch (Exception e) {
                return java.util.stream.Stream.empty();
            }
        }).toList()));
        result.setUpdatedAt(LocalDateTime.now());

        session.setResult(result);
    }

    private VoiceSessionResponse toSessionResponse(VoiceInterviewSession session) {
        return VoiceSessionResponse.builder()
            .sessionId(session.getId())
            .topic(session.getTopic())
            .targetRole(session.getTargetRole())
            .difficulty(session.getDifficulty())
            .status(session.getStatus())
            .durationSeconds(session.getDurationSeconds())
            .startedAt(session.getStartedAt())
            .completedAt(session.getCompletedAt())
            .build();
    }

    private VoiceSessionSummaryResponse toSummaryResponse(VoiceInterviewSession session) {
        List<VoiceAnswerResponse> history = session.getAnswers().stream()
            .map(this::toAnswerResponse)
            .collect(Collectors.toList());

        List<String> conversationHistory = conversationMemoryService.getConversationHistory(session);
        VoiceInterviewResult result = session.getResult();

        return VoiceSessionSummaryResponse.builder()
            .sessionId(session.getId())
            .topic(session.getTopic())
            .targetRole(session.getTargetRole())
            .difficulty(session.getDifficulty())
            .status(session.getStatus())
            .durationSeconds(session.getDurationSeconds())
            .startedAt(session.getStartedAt())
            .completedAt(session.getCompletedAt())
            .averageScore(result != null ? result.getAverageScore() : null)
            .averageConfidence(result != null ? result.getAverageConfidence() : null)
            .averageCommunication(result != null ? result.getAverageCommunication() : null)
            .averageClarity(result != null ? result.getAverageClarity() : null)
            .averageGrammar(result != null ? result.getAverageGrammar() : null)
            .averageTechnical(result != null ? result.getAverageTechnical() : null)
            .averageStar(result != null ? result.getAverageStar() : null)
            .averageCompleteness(result != null ? result.getAverageCompleteness() : null)
            .averageWpm(result != null ? result.getAverageWpm() : null)
            .answerHistory(history)
            .conversationMemory(conversationHistory)
            .build();
    }

    private VoiceAnswerResponse toAnswerResponse(VoiceAnswer answer) {
        return VoiceAnswerResponse.builder()
            .answerId(answer.getId())
            .sessionId(answer.getSession().getId())
            .transcript(answer.getTranscript())
            .wordCount(answer.getWordCount())
            .fillerWordCount(answer.getFillerWordCount())
            .speakingRateWpm(answer.getSpeakingRateWpm())
            .confidenceScore(answer.getConfidenceScore())
            .communicationScore(answer.getCommunicationScore())
            .clarityScore(answer.getClarityScore())
            .grammarScore(answer.getGrammarScore())
            .technicalScore(answer.getTechnicalScore())
            .starScore(answer.getStarScore())
            .completenessScore(answer.getCompletenessScore())
            .overallScore(answer.getOverallScore())
            .modelAnswer(answer.getModelAnswer())
            .interviewerFeedback(answer.getInterviewerFeedback())
            .strengths(parseList(answer.getStrengthsJson()))
            .improvements(parseList(answer.getImprovementsJson()))
            .missingPoints(parseList(answer.getMissingPointsJson()))
            .nextQuestion(answer.getNextQuestion())
            .createdAt(answer.getCreatedAt())
            .build();
    }

    private String cleanJson(String raw) {
        String s = raw.trim();
        if (s.startsWith("```json")) s = s.substring(7);
        else if (s.startsWith("```")) s = s.substring(3);
        if (s.endsWith("```")) s = s.substring(0, s.length() - 3);
        return s.trim();
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> parseList(String json) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
