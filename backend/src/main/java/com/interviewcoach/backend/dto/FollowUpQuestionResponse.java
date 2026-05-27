package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.FollowUpQuestion;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowUpQuestionResponse {
    private Long id;
    private String question;
    private String followUpType;
    private String triggerReason;
    private String difficultyLevel;
    private String coachingHint;
    private Boolean isAnswered;
    private String candidateAnswer;
    private Double answerQualityScore;

    public static FollowUpQuestionResponse from(FollowUpQuestion question) {
        return FollowUpQuestionResponse.builder()
            .id(question.getId())
            .question(question.getQuestion())
            .followUpType(question.getFollowUpType())
            .triggerReason(question.getTriggerReason())
            .difficultyLevel(question.getDifficultyLevel())
            .coachingHint(question.getCoachingHint())
            .isAnswered(question.getIsAnswered())
            .candidateAnswer(question.getCandidateAnswer())
            .answerQualityScore(question.getAnswerQualityScore())
            .build();
    }
}
