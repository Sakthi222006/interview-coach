package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.AIEvaluationService;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.EvaluationResult;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.dto.AnswerResponse;
import com.interviewcoach.backend.dto.SubmitAnswerRequest;
import com.interviewcoach.backend.model.*;
import com.interviewcoach.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {

    private final SessionAnswerRepository    answerRepository;
    private final InterviewSessionRepository sessionRepository;
    private final QuestionRepository         questionRepository;
    private final AIEvaluationService         aiEvaluationService;
    private final PromptBuilder               promptBuilder;
    private final GeminiClient                geminiClient;

    @Transactional
    public AnswerResponse submitAnswer(Long sessionId, SubmitAnswerRequest req, Long userId) {

        // 1. Verify session belongs to user
        InterviewSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new AccessDeniedException("Session not found or access denied"));

        if (!"IN_PROGRESS".equals(session.getStatus())) {
            throw new RuntimeException("Session is not in progress");
        }

        // 2. Load the question
        Question question = questionRepository.findById(req.getQuestionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));

        // 3. Evaluate the answer
        boolean isCorrect = false;
        if ("MCQ".equals(question.getQuestionType()) && question.getCorrectAnswer() != null) {
            isCorrect = question.getCorrectAnswer()
                .equalsIgnoreCase(req.getUserAnswer() != null ? req.getUserAnswer().trim() : "");
        }

        log.info("submitAnswer called: sessionId={}, questionId={}, questionType={}, userId={}, isCorrect={}",
            sessionId, question.getId(), question.getQuestionType(), userId, isCorrect);

        if ("TEXT".equals(question.getQuestionType())) {
            log.info("TEXT answer detected for questionId={} userAnswer='{}'", question.getId(), req.getUserAnswer());
        }

        // 4. Save the answer
        SessionAnswer answer = SessionAnswer.builder()
            .session(session)
            .question(question)
            .userAnswer(req.getUserAnswer())
            .isCorrect(isCorrect)
            .timeSpentSeconds(req.getTimeSpentSeconds() != null ? req.getTimeSpentSeconds() : 0)
            .answeredAt(LocalDateTime.now())
            .build();

        answer = answerRepository.save(answer);

        // ── Phase 5: AI Evaluation for TEXT questions ──────────────
        EvaluationResult evaluation = null;
        if ("TEXT".equals(question.getQuestionType())) {
            try {
                log.info("Calling Gemini service for questionId={}", question.getId());
                evaluation = aiEvaluationService.evaluate(question, req.getUserAnswer());

                if (evaluation == null) {
                    log.warn("AI evaluation returned null for questionId={}", question.getId());
                } else if (!evaluation.isSuccess()) {
                    log.warn("AI evaluation failed for questionId={} error={}", question.getId(), evaluation.getErrorMessage());
                    answer.setConfidenceScore(0.0);
                    answer.setAiFeedback(evaluation.getErrorMessage());
                    answer.setInterviewerFeedback(evaluation.getErrorMessage());
                    answer = answerRepository.save(answer);
                } else {
                    log.info("Gemini response received for questionId={} overallScore={} confidence={} aiFeedbackPresent={}",
                        question.getId(), evaluation.getOverallScore(), evaluation.getConfidence(), evaluation.getInterviewerFeedback() != null);
                    answer.setAiScore(             evaluation.getOverallScore());
                    answer.setTechnicalScore(      evaluation.getTechnicalAccuracy());
                    answer.setCommunicationScore(  evaluation.getCommunication());
                    answer.setProblemSolvingScore( evaluation.getProblemSolving());
                    answer.setConfidenceScore(     (double) evaluation.getConfidence());
                    answer.setModelAnswer(         evaluation.getModelAnswer());
                    answer.setAiFeedback(          evaluation.getInterviewerFeedback());
                    answer.setInterviewerFeedback( evaluation.getInterviewerFeedback());
                    answer.setStrengthsJson(       toJson(evaluation.getStrengths()));
                    answer.setImprovementsJson(    toJson(evaluation.getImprovements()));
                    answer.setMissingConceptsJson( toJson(evaluation.getMissingConcepts()));
                    answer.setAiEvaluated(true);
                    answer.setEvaluatedAt(LocalDateTime.now());
                    log.info("Saving AI evaluation into SessionAnswer answerId={}", answer.getId());
                    answer = answerRepository.save(answer);
                }

                // ── Phase 6: STAR scoring for HR behavioural answers ───────
                if ("TEXT".equals(question.getQuestionType())
                        && "HR".equalsIgnoreCase(question.getTopic())) {
                    try {
                        String starPrompt = promptBuilder.buildStarEvaluationPrompt(
                            question, req.getUserAnswer()
                        );
                        String starRaw = geminiClient.callGemini(starPrompt);
                        if (starRaw != null && !starRaw.isBlank()) {
                            String cleaned = cleanStarJson(starRaw);
                            JsonNode node = new ObjectMapper().readTree(cleaned);
                            answer.setStarSituationScore(node.path("starSituationScore").asInt(0));
                            answer.setStarTaskScore(     node.path("starTaskScore").asInt(0));
                            answer.setStarActionScore(   node.path("starActionScore").asInt(0));
                            answer.setStarResultScore(   node.path("starResultScore").asInt(0));
                            answer.setStarTotalScore(    node.path("starTotalScore").asInt(0));
                            answerRepository.save(answer);
                        }
                    } catch (Exception starEx) {
                        log.warn("STAR evaluation failed (non-critical): {}", starEx.getMessage());
                    }
                }
            } catch (Exception ex) {
                log.error("AI evaluation exception for sessionId={} questionId={} userAnswer='{}'. This error will not prevent answer persistence.",
                    sessionId, question.getId(), req.getUserAnswer(), ex);
            }
        }

        // 5. Update session progress counter
        session.setAnsweredQuestions(session.getAnsweredQuestions() + 1);
        sessionRepository.save(session);

        log.info("Returning AI evaluation for answerId={} questionId={} evaluationSuccess={}",
            answer.getId(), question.getId(), evaluation != null && evaluation.isSuccess());

        return AnswerResponse.builder()
            .answerId(answer.getId())
            .isCorrect(isCorrect)
            .correctAnswer(question.getCorrectAnswer())
            .explanation(question.getExplanation())
            .questionType(question.getQuestionType())
            .confidenceScore(evaluation != null ? evaluation.getConfidence() : 0.0)
            .aiFeedback(evaluation != null
                ? (evaluation.isSuccess() ? evaluation.getInterviewerFeedback() : evaluation.getErrorMessage())
                : null)
            .evaluation(evaluation)
            .build();
    }

    private String toJson(List<String> list) {
        try {
            return new ObjectMapper().writeValueAsString(list);
        } catch (Exception e) {
            return "[]";
        }
    }

    private String cleanStarJson(String raw) {
        String s = raw.trim();
        if (s.startsWith("```json")) s = s.substring(7);
        else if (s.startsWith("```")) s = s.substring(3);
        if (s.endsWith("```")) s = s.substring(0, s.length() - 3);
        return s.trim();
    }
}
