package com.interviewcoach.backend.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {

    private int          overallScore;
    private int          technicalAccuracy;
    private int          communication;
    private int          problemSolving;
    private int          confidence;
    private List<String> strengths;
    private List<String> improvements;
    private List<String> missingConcepts;
    private String       modelAnswer;
    private String       interviewerFeedback;

    @Builder.Default
    private boolean success = true;

    private String errorMessage;

    public static EvaluationResult failed(String reason) {
        return EvaluationResult.builder()
            .success(false)
            .errorMessage(reason)
            .overallScore(0)
            .build();
    }
}
