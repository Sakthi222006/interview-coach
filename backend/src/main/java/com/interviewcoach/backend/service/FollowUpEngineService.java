package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.ai.PromptBuilder;
import com.interviewcoach.backend.model.FollowUpQuestion;
import com.interviewcoach.backend.model.InterviewConversation;
import com.interviewcoach.backend.model.InterviewScenario;
import com.interviewcoach.backend.repository.FollowUpQuestionRepository;
import com.interviewcoach.backend.repository.InterviewConversationRepository;
import com.interviewcoach.backend.repository.InterviewScenarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowUpEngineService {

    private final InterviewScenarioRepository scenarioRepository;
    private final InterviewConversationRepository conversationRepository;
    private final FollowUpQuestionRepository followUpQuestionRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public void generateFollowUps(
        Long scenarioId,
        InterviewConversation lastConversation,
        String candidateAnswer,
        Double answerQualityScore
    ) {
        InterviewScenario scenario = scenarioRepository.findById(scenarioId)
            .orElseThrow(() -> new RuntimeException("Scenario not found"));

        // Determine follow-up type and trigger reason
        String followUpType;
        String triggerReason;
        String difficultyLevel;

        if (answerQualityScore < 40) {
            followUpType = "COACHING";
            triggerReason = "WEAK_ANSWER";
            difficultyLevel = "EASY";
        } else if (answerQualityScore > 80) {
            followUpType = "CHALLENGE";
            triggerReason = "STRONG_ANSWER";
            difficultyLevel = "HARD";
        } else if (answerQualityScore < 60) {
            followUpType = "DEEPER_DIVE";
            triggerReason = "INCOMPLETE_ANSWER";
            difficultyLevel = "MEDIUM";
        } else {
            followUpType = "DEEPER_DIVE";
            triggerReason = "UNCLEAR_ANSWER";
            difficultyLevel = "MEDIUM";
        }

        // Generate follow-up question using AI
        String followUpQuestion = generateFollowUpQuestion(
            scenario,
            candidateAnswer,
            followUpType,
            triggerReason,
            difficultyLevel
        );

        // Generate coaching hint for weak answers
        String coachingHint = null;
        if (answerQualityScore < 50) {
            coachingHint = generateCoachingHint(scenario, candidateAnswer);
        }

        // Create follow-up question entity
        FollowUpQuestion followUp = FollowUpQuestion.builder()
            .scenario(scenario)
            .parentConversation(lastConversation)
            .question(followUpQuestion)
            .followUpType(followUpType)
            .triggerReason(triggerReason)
            .difficultyLevel(difficultyLevel)
            .coachingHint(coachingHint)
            .isAnswered(false)
            .answerQualityScore(0.0)
            .build();

        followUpQuestionRepository.save(followUp);
        log.info("Generated follow-up question for scenario {}", scenarioId);
    }

    private String generateFollowUpQuestion(
        InterviewScenario scenario,
        String candidateAnswer,
        String followUpType,
        String triggerReason,
        String difficultyLevel
    ) {
        try {
            String prompt = buildFollowUpPrompt(
                scenario,
                candidateAnswer,
                followUpType,
                triggerReason,
                difficultyLevel
            );
            String response = geminiClient.callGemini(prompt);
            return parseFollowUpResponse(response);
        } catch (Exception e) {
            log.error("Failed to generate follow-up question", e);
            return getDefaultFollowUp(followUpType);
        }
    }

    private String generateCoachingHint(InterviewScenario scenario, String candidateAnswer) {
        try {
            String prompt = "Based on this interview scenario:\n" +
                "Round: " + scenario.getRoundType() + "\n" +
                "Context: " + scenario.getScenarioContext() + "\n\n" +
                "The candidate answered: \"" + candidateAnswer + "\"\n\n" +
                "Provide a brief coaching hint (1-2 sentences) to help them improve. Be supportive and constructive.";
            String response = geminiClient.callGemini(prompt);
            return parseHintResponse(response);
        } catch (Exception e) {
            log.error("Failed to generate coaching hint", e);
            return "Think about the key concepts involved and try to structure your answer more clearly.";
        }
    }

    private String buildFollowUpPrompt(
        InterviewScenario scenario,
        String candidateAnswer,
        String followUpType,
        String triggerReason,
        String difficultyLevel
    ) {
        return "You are a professional recruiter conducting an interview.\n\n" +
            "Interview Context:\n" +
            "Round Type: " + scenario.getRoundType() + "\n" +
            "Recruiter Type: " + scenario.getRecruiterProfile().getRecruiterType() + "\n" +
            "Job Description: " + scenario.getJobDescription() + "\n" +
            "Scenario: " + scenario.getScenarioContext() + "\n\n" +
            "Candidate's Previous Answer: \"" + candidateAnswer + "\"\n\n" +
            "You need to ask a " + followUpType + " follow-up question.\n" +
            "Reason: " + triggerReason + "\n" +
            "Difficulty Level: " + difficultyLevel + "\n\n" +
            "Generate a professional, natural follow-up question. Return ONLY the question text, no other formatting.";
    }

    private String parseFollowUpResponse(String response) {
        if (response == null || response.isBlank()) {
            return getDefaultFollowUp("DEEPER_DIVE");
        }
        return response.trim();
    }

    private String parseHintResponse(String response) {
        if (response == null || response.isBlank()) {
            return "Think about the key concepts and try to structure your answer more clearly.";
        }
        return response.trim();
    }

    private String getDefaultFollowUp(String followUpType) {
        switch (followUpType) {
            case "COACHING":
                return "Could you walk me through your thinking step by step? What was your first approach?";
            case "CHALLENGE":
                return "Great answer! Now, how would you handle this with additional constraints or edge cases?";
            case "DEEPER_DIVE":
                return "Can you elaborate more on that point? What are the trade-offs you considered?";
            default:
                return "Can you provide more details about that?";
        }
    }
}
