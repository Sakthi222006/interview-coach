package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SessionReviewResponse {
    private Long sessionId;
    private String topic;
    private String difficulty;
    private Double finalScore;
    private Integer durationSeconds;
    private List<AnswerReview> answers;

    @Data
    @Builder
    public static class AnswerReview {
        private int questionNumber;
        private String questionText;
        private String questionType;
        private String difficulty;
        private String userAnswer;
        private boolean isCorrect;
        private String correctAnswer;
        private String explanation;
        private Integer timeSpentSeconds;
        private Integer aiOverallScore;
        private Integer technicalScore;
        private Integer communicationScore;
        private Integer confidenceScore;
        private String interviewerFeedback;
        private List<String> strengths;
        private List<String> improvements;
        private Integer starTotalScore;
        private Integer starSituationScore;
        private Integer starTaskScore;
        private Integer starActionScore;
        private Integer starResultScore;
    }
}
