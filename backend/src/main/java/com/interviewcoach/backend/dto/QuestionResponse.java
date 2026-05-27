package com.interviewcoach.backend.dto;

import com.interviewcoach.backend.model.Question;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResponse {
    private Long   id;
    private String topic;
    private String difficulty;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String questionType;
    private String tags;
    // NOTE: correctAnswer is intentionally EXCLUDED from this DTO
    // We never send the answer to the frontend before the user submits

    public static QuestionResponse from(Question q) {
        return QuestionResponse.builder()
            .id(q.getId())
            .topic(q.getTopic())
            .difficulty(q.getDifficulty())
            .questionText(q.getQuestionText())
            .optionA(q.getOptionA())
            .optionB(q.getOptionB())
            .optionC(q.getOptionC())
            .optionD(q.getOptionD())
            .questionType(q.getQuestionType())
            .tags(q.getTags())
            .build();
    }
}