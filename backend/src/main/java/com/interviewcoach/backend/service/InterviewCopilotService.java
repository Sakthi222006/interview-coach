package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.ai.GeminiClient;
import com.interviewcoach.backend.dto.*;
import com.interviewcoach.backend.model.*;
import com.interviewcoach.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewCopilotService {

    private final InterviewScenarioRepository scenarioRepository;
    private final InterviewConversationRepository conversationRepository;
    private final FollowUpQuestionRepository followUpQuestionRepository;
    private final RecruiterSimulationService recruiterSimulationService;
    private final FollowUpEngineService followUpEngineService;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public String getNextQuestion(Long scenarioId, Long userId) {
        InterviewScenario scenario = scenarioRepository.findByIdAndUserId(scenarioId, userId)
            .orElseThrow(() -> new AccessDeniedException("Scenario not found or access denied"));

        // If there are unanswered follow-ups, return the first one
        List<FollowUpQuestion> unansweredFollowUps = followUpQuestionRepository
            .findByScenarioIdAndIsAnswered(scenarioId, false);

        if (!unansweredFollowUps.isEmpty()) {
            return unansweredFollowUps.get(0).getQuestion();
        }

        // Otherwise, generate the next main question
        if (scenario.getCurrentRoundIndex() < scenario.getTotalRounds()) {
            String question = generateMainQuestion(scenario);
            return question;
        }

        // Interview completed
        return null;
    }

    @Transactional
    public InterviewConversationResponse processAnswer(
        Long scenarioId,
        Long userId,
        CandidateAnswerRequest request
    ) {
        InterviewScenario scenario = scenarioRepository.findByIdAndUserId(scenarioId, userId)
            .orElseThrow(() -> new AccessDeniedException("Scenario not found or access denied"));

        // Evaluate the candidate's answer
        AnswerEvaluation evaluation = evaluateAnswer(scenario, request.getAnswer());

        // Create conversation entry
        Integer turnNumber = Math.toIntExact(conversationRepository.countByScenarioId(scenarioId) + 1);

        InterviewConversation conversation = InterviewConversation.builder()
            .scenario(scenario)
            .turnNumber(turnNumber)
            .speaker("CANDIDATE")
            .message(request.getAnswer())
            .recruiterReaction(evaluation.getRecruiterReaction())
            .answerAssessment(evaluation.getAssessment())
            .technicalAccuracyScore(evaluation.getTechnicalScore())
            .communicationClarityScore(evaluation.getCommunicationScore())
            .problemSolvingScore(evaluation.getProblemSolvingScore())
            .confidenceLevelScore(Double.valueOf(request.getConfidenceLevel() * 10))
            .aiComment(evaluation.getComment())
            .build();

        conversation = conversationRepository.save(conversation);

        // Update scenario scores (moving average)
        updateScenarioScores(scenario, evaluation);

        // Generate follow-up questions if needed
        if (scenario.getCurrentRoundIndex() < scenario.getTotalRounds()) {
            followUpEngineService.generateFollowUps(
                scenarioId,
                conversation,
                request.getAnswer(),
                evaluation.getOverallScore()
            );
        }

        // Move to next round
        scenario.setCurrentRoundIndex(scenario.getCurrentRoundIndex() + 1);
        scenarioRepository.save(scenario);

        log.info("Processed answer for scenario {}, turn {}", scenarioId, turnNumber);
        return InterviewConversationResponse.from(conversation);
    }

    @Transactional
    public List<InterviewConversationResponse> getConversationHistory(Long scenarioId, Long userId) {
        InterviewScenario scenario = scenarioRepository.findByIdAndUserId(scenarioId, userId)
            .orElseThrow(() -> new AccessDeniedException("Scenario not found or access denied"));

        return conversationRepository.findByScenarioIdOrderByTurnNumberAsc(scenarioId)
            .stream()
            .map(InterviewConversationResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public InterviewSummaryResponse getInterviewSummary(Long scenarioId, Long userId) {
        InterviewScenario scenario = scenarioRepository.findByIdAndUserId(scenarioId, userId)
            .orElseThrow(() -> new AccessDeniedException("Scenario not found or access denied"));

        Long totalTurns = conversationRepository.countByScenarioId(scenarioId);
        String summary = generateHiringSummary(scenario);

        String[] parts = summary.split("\\|");
        String keyStrengths = parts.length > 0 ? parts[0].trim() : "";
        String areasForImprovement = parts.length > 1 ? parts[1].trim() : "";

        return InterviewSummaryResponse.builder()
            .scenarioId(scenarioId)
            .roundType(scenario.getRoundType())
            .recruiterName(scenario.getRecruiterProfile().getRecruiterName())
            .technicalScore(scenario.getTechnicalScore())
            .communicationScore(scenario.getCommunicationScore())
            .confidenceScore(scenario.getConfidenceScore())
            .leadershipScore(scenario.getLeadershipScore())
            .problemSolvingScore(scenario.getProblemSolvingScore())
            .overallScore(scenario.getOverallScore())
            .hireRecommendation(scenario.getHireRecommendation())
            .hiringComments(scenario.getHiringComments())
            .durationSeconds(scenario.getDurationSeconds())
            .totalTurns(totalTurns.intValue())
            .keyStrengths(keyStrengths)
            .areasForImprovement(areasForImprovement)
            .build();
    }

    private String generateMainQuestion(InterviewScenario scenario) {
        try {
            String prompt = buildQuestionPrompt(scenario);
            String response = geminiClient.callGemini(prompt);
            return parseQuestionResponse(response);
        } catch (Exception e) {
            log.error("Failed to generate question for scenario {}", scenario.getId(), e);
            return getDefaultQuestion(scenario.getRoundType());
        }
    }

    private AnswerEvaluation evaluateAnswer(InterviewScenario scenario, String candidateAnswer) {
        try {
            String prompt = buildEvaluationPrompt(scenario, candidateAnswer);
            String response = geminiClient.callGemini(prompt);
            return parseEvaluationResponse(response);
        } catch (Exception e) {
            log.error("Failed to evaluate answer for scenario {}", scenario.getId(), e);
            return AnswerEvaluation.builder()
                .technicalScore(50.0)
                .communicationScore(50.0)
                .problemSolvingScore(50.0)
                .overallScore(50.0)
                .recruiterReaction("That's interesting. Tell me more about your approach.")
                .assessment("Your answer shows understanding of the topic.")
                .comment("Good start. Consider elaborating on key concepts.")
                .build();
        }
    }

    private String generateHiringSummary(InterviewScenario scenario) {
        try {
            String prompt = buildHiringSummaryPrompt(scenario);
            return geminiClient.callGemini(prompt);
        } catch (Exception e) {
            log.error("Failed to generate hiring summary for scenario {}", scenario.getId(), e);
            return "Demonstrated solid technical understanding. | Focus on improving communication clarity.";
        }
    }

    private String buildQuestionPrompt(InterviewScenario scenario) {
        String recruiterPersonality = scenario.getRecruiterProfile().getPersonality();
        String recruiterStyle = scenario.getRecruiterProfile().getInterviewStyle();
        int questionNumber = scenario.getCurrentRoundIndex() + 1;

        return "You are " + scenario.getRecruiterProfile().getRecruiterName() + ", a professional recruiter.\n\n" +
            "Personality: " + recruiterPersonality + "\n" +
            "Interview Style: " + recruiterStyle + "\n\n" +
            "Interview Details:\n" +
            "Round Type: " + scenario.getRoundType() + "\n" +
            "Job Description: " + scenario.getJobDescription() + "\n" +
            "Scenario Context: " + scenario.getScenarioContext() + "\n\n" +
            "This is question " + questionNumber + " of " + scenario.getTotalRounds() + ".\n\n" +
            "Generate a professional interview question appropriate for this " + scenario.getRoundType() + " round.\n" +
            "Return ONLY the question text, no other formatting or introduction.";
    }

    private String buildEvaluationPrompt(InterviewScenario scenario, String candidateAnswer) {
        return "You are evaluating a candidate's answer in an interview.\n\n" +
            "Round Type: " + scenario.getRoundType() + "\n" +
            "Job Context: " + scenario.getJobDescription() + "\n" +
            "Scenario: " + scenario.getScenarioContext() + "\n\n" +
            "Candidate's Answer: \"" + candidateAnswer + "\"\n\n" +
            "Provide evaluation in JSON format with:\n" +
            "- technical_score (0-100): How technically accurate and complete?\n" +
            "- communication_score (0-100): How clearly was it communicated?\n" +
            "- problem_solving_score (0-100): Quality of approach and reasoning?\n" +
            "- recruiter_reaction: What would the interviewer say in response? (2-3 sentences)\n" +
            "- assessment: Detailed assessment of the answer (2-3 sentences)\n" +
            "- comment: Supportive coaching comment (1-2 sentences)\n" +
            "Return valid JSON only.";
    }

    private String buildHiringSummaryPrompt(InterviewScenario scenario) {
        return "Based on this interview:\n" +
            "Round: " + scenario.getRoundType() + "\n" +
            "Recruiter: " + scenario.getRecruiterProfile().getRecruiterName() + "\n" +
            "Overall Score: " + scenario.getOverallScore() + "/100\n" +
            "Technical Score: " + scenario.getTechnicalScore() + "/100\n" +
            "Communication Score: " + scenario.getCommunicationScore() + "/100\n\n" +
            "Summarize key strengths and areas for improvement separated by |. " +
            "Format: [Strengths] | [Areas for Improvement]";
    }

    private void updateScenarioScores(InterviewScenario scenario, AnswerEvaluation evaluation) {
        int answerCount = Math.max(1, scenario.getCurrentRoundIndex());

        // Update scores with moving average
        scenario.setTechnicalScore(updateMovingAverage(
            scenario.getTechnicalScore(),
            evaluation.getTechnicalScore(),
            answerCount
        ));
        scenario.setCommunicationScore(updateMovingAverage(
            scenario.getCommunicationScore(),
            evaluation.getCommunicationScore(),
            answerCount
        ));
        scenario.setProblemSolvingScore(updateMovingAverage(
            scenario.getProblemSolvingScore(),
            evaluation.getProblemSolvingScore(),
            answerCount
        ));

        // Leadership score (for manager/lead rounds)
        if (scenario.getRoundType().contains("MANAGERIAL")) {
            scenario.setLeadershipScore(updateMovingAverage(
                scenario.getLeadershipScore(),
                evaluation.getTechnicalScore() * 0.8,
                answerCount
            ));
        }

        // Confidence score
        scenario.setConfidenceScore(updateMovingAverage(
            scenario.getConfidenceScore(),
            65.0,  // Default confidence
            answerCount
        ));

        // Overall score
        double overallScore = (scenario.getTechnicalScore() +
            scenario.getCommunicationScore() +
            scenario.getConfidenceScore() +
            scenario.getLeadershipScore() +
            scenario.getProblemSolvingScore()) / 5.0;
        scenario.setOverallScore(Math.round(overallScore * 10.0) / 10.0);

        // Hiring recommendation based on overall score
        if (scenario.getOverallScore() >= 75) {
            scenario.setHireRecommendation("HIRE");
            scenario.setHiringComments("Strong candidate with excellent performance across all areas.");
        } else if (scenario.getOverallScore() >= 60) {
            scenario.setHireRecommendation("ON_HOLD");
            scenario.setHiringComments("Good potential. Consider additional rounds or interviews.");
        } else {
            scenario.setHireRecommendation("NO_HIRE");
            scenario.setHiringComments("Does not meet the required competency level at this time.");
        }
    }

    private Double updateMovingAverage(Double currentAverage, Double newValue, int count) {
        if (count <= 1) {
            return Math.round(newValue * 10.0) / 10.0;
        }
        double updated = (currentAverage * (count - 1) + newValue) / count;
        return Math.round(updated * 10.0) / 10.0;
    }

    private String parseQuestionResponse(String response) {
        if (response == null || response.isBlank()) {
            return "Tell me about your experience with this type of challenge.";
        }
        return response.trim();
    }

    private AnswerEvaluation parseEvaluationResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            double technicalScore = root.path("technical_score").asDouble(50.0);
            double communicationScore = root.path("communication_score").asDouble(50.0);
            double problemSolvingScore = root.path("problem_solving_score").asDouble(50.0);
            String recruiterReaction = root.path("recruiter_reaction").asText(
                "That's interesting. Tell me more."
            );
            String assessment = root.path("assessment").asText(
                "Your answer demonstrates understanding of the topic."
            );
            String comment = root.path("comment").asText(
                "Good effort. Consider discussing trade-offs."
            );

            double overallScore = (technicalScore + communicationScore + problemSolvingScore) / 3.0;

            return AnswerEvaluation.builder()
                .technicalScore(Math.round(technicalScore * 10.0) / 10.0)
                .communicationScore(Math.round(communicationScore * 10.0) / 10.0)
                .problemSolvingScore(Math.round(problemSolvingScore * 10.0) / 10.0)
                .overallScore(Math.round(overallScore * 10.0) / 10.0)
                .recruiterReaction(recruiterReaction)
                .assessment(assessment)
                .comment(comment)
                .build();
        } catch (Exception e) {
            log.error("Failed to parse evaluation response", e);
            return AnswerEvaluation.builder()
                .technicalScore(50.0)
                .communicationScore(50.0)
                .problemSolvingScore(50.0)
                .overallScore(50.0)
                .recruiterReaction("Interesting. Tell me more about your approach.")
                .assessment("Your answer shows understanding.")
                .comment("Keep going. You're on the right track.")
                .build();
        }
    }

    private String getDefaultQuestion(String roundType) {
        switch (roundType) {
            case "HR_ROUND":
                return "Tell me about your background and what attracted you to this role.";
            case "TECHNICAL_ROUND":
                return "Walk me through your approach to solving technical challenges.";
            case "MANAGERIAL_ROUND":
                return "Describe your leadership style and how you handle team conflicts.";
            case "SYSTEM_DESIGN_ROUND":
                return "Design a scalable system for a real-world problem.";
            case "BEHAVIORAL_ROUND":
                return "Tell me about a time when you overcame a significant challenge.";
            default:
                return "Tell me more about your experience.";
        }
    }

    // Helper class for answer evaluation
    @lombok.Data
    @lombok.Builder
    public static class AnswerEvaluation {
        private Double technicalScore;
        private Double communicationScore;
        private Double problemSolvingScore;
        private Double overallScore;
        private String recruiterReaction;
        private String assessment;
        private String comment;
    }
}
