
package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.InterviewSession;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SessionResponse {
    private Long              id;
    private String            topic;
    private String            difficulty;
    private String            status;
    private Integer           totalQuestions;
    private Integer           answeredQuestions;
    private Double            score;
    private LocalDateTime     startedAt;
    private LocalDateTime     completedAt;
    private Integer           durationSeconds;
    private List<QuestionResponse> questions; // included on session create only

    public static SessionResponse from(InterviewSession s) {
        return SessionResponse.builder()
            .id(s.getId())
            .topic(s.getTopic())
            .difficulty(s.getDifficulty())
            .status(s.getStatus())
            .totalQuestions(s.getTotalQuestions())
            .answeredQuestions(s.getAnsweredQuestions())
            .score(s.getScore())
            .startedAt(s.getStartedAt())
            .completedAt(s.getCompletedAt())
            .durationSeconds(s.getDurationSeconds())
            .build();
    }
}