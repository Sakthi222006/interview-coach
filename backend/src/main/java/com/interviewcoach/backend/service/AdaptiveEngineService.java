package com.interviewcoach.backend.service;

import com.interviewcoach.backend.dto.AdaptiveRecommendationResponse;
import com.interviewcoach.backend.model.InterviewSession;
import com.interviewcoach.backend.repository.InterviewSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdaptiveEngineService {

    private final InterviewSessionRepository sessionRepository;

    public AdaptiveRecommendationResponse recommend(Long userId, String topic) {
        List<InterviewSession> recent =
            sessionRepository.findTop3ByUserIdAndTopicAndStatusOrderByStartedAtDesc(
                userId, topic.toUpperCase(), "COMPLETED"
            );

        if (recent.isEmpty()) {
            return AdaptiveRecommendationResponse.builder()
                .topic(topic)
                .recommendedDifficulty("EASY")
                .reason("No previous sessions found for " + topic + ". Start with Easy to build confidence.")
                .rollingAverage(0.0)
                .sessionsSampled(0)
                .hasHistory(false)
                .build();
        }

        double rolling = recent.stream()
            .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0)
            .average().orElse(0);
        double rounded = Math.round(rolling * 10.0) / 10.0;

        String difficulty;
        String reason;

        if (rolling >= 75) {
            difficulty = "HARD";
            reason = String.format(
                "Your last %d %s sessions averaged %.1f%%. Time to challenge yourself with Hard questions.",
                recent.size(), topic, rounded);
        } else if (rolling >= 50) {
            difficulty = "MEDIUM";
            reason = String.format(
                "Your last %d %s sessions averaged %.1f%%. Medium difficulty is the right next step.",
                recent.size(), topic, rounded);
        } else {
            difficulty = "EASY";
            reason = String.format(
                "Your last %d %s sessions averaged %.1f%%. Consolidate Easy questions before moving up.",
                recent.size(), topic, rounded);
        }

        return AdaptiveRecommendationResponse.builder()
            .topic(topic)
            .recommendedDifficulty(difficulty)
            .reason(reason)
            .rollingAverage(rounded)
            .sessionsSampled(recent.size())
            .hasHistory(true)
            .build();
    }
}
