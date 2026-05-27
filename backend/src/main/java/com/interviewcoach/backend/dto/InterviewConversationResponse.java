package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.InterviewConversation;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InterviewConversationResponse {
    private Long id;
    private Integer turnNumber;
    private String speaker;
    private String message;
    private String recruiterReaction;
    private String answerAssessment;
    private Double technicalAccuracyScore;
    private Double communicationClarityScore;
    private Double problemSolvingScore;
    private Double confidenceLevelScore;
    private String aiComment;
    private LocalDateTime createdAt;
    private List<FollowUpQuestionResponse> followUpQuestions;

    public static InterviewConversationResponse from(InterviewConversation conversation) {
        return InterviewConversationResponse.builder()
            .id(conversation.getId())
            .turnNumber(conversation.getTurnNumber())
            .speaker(conversation.getSpeaker())
            .message(conversation.getMessage())
            .recruiterReaction(conversation.getRecruiterReaction())
            .answerAssessment(conversation.getAnswerAssessment())
            .technicalAccuracyScore(conversation.getTechnicalAccuracyScore())
            .communicationClarityScore(conversation.getCommunicationClarityScore())
            .problemSolvingScore(conversation.getProblemSolvingScore())
            .confidenceLevelScore(conversation.getConfidenceLevelScore())
            .aiComment(conversation.getAiComment())
            .createdAt(conversation.getCreatedAt())
            .followUpQuestions(conversation.getFollowUpQuestions().stream()
                .map(FollowUpQuestionResponse::from)
                .toList())
            .build();
    }
}
