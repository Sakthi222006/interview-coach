package com.interviewcoach.backend.service;

import com.interviewcoach.backend.dto.RoadmapResponse;
import com.interviewcoach.backend.model.InterviewSession;
import com.interviewcoach.backend.repository.InterviewSessionRepository;
import com.interviewcoach.backend.repository.SessionAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final InterviewSessionRepository sessionRepository;
    private final SessionAnswerRepository    answerRepository;

    public RoadmapResponse generateRoadmap(Long userId) {
        long completed = sessionRepository.countByUserIdAndStatus(userId, "COMPLETED");

        Double avgScore = sessionRepository.findAverageScoreByUserId(userId);
        int readiness = avgScore != null ? (int) Math.round(avgScore) : 0;

        String level = readiness >= 75 ? "Advanced"
                     : readiness >= 50 ? "Intermediate"
                     :                   "Beginner";

        List<RoadmapResponse.RoadmapItem> items = new ArrayList<>();

        String weakestTopic = findWeakestTopic(userId);
        if (weakestTopic != null) {
            items.add(RoadmapResponse.RoadmapItem.builder()
                .priority(1)
                .type("TOPIC")
                .title("Practice " + weakestTopic + " intensively")
                .description("Your " + weakestTopic + " scores are below 60%. "
                    + "Complete 3 focused " + weakestTopic + " sessions — "
                    + "start with Easy, then advance. Focus on understanding "
                    + "why each wrong answer was incorrect.")
                .metric("Target: 70%+ average on " + weakestTopic)
                .icon("📚")
                .build());
        }

        String weakestDimension = findWeakestDimension(userId);
        items.add(RoadmapResponse.RoadmapItem.builder()
            .priority(2)
            .type("SKILL")
            .title("Improve your " + weakestDimension)
            .description(getDimensionAdvice(weakestDimension))
            .metric("Target: 70%+ on " + weakestDimension + " in AI evaluation")
            .icon(getDimensionIcon(weakestDimension))
            .build());

        if (completed < 5) {
            items.add(RoadmapResponse.RoadmapItem.builder()
                .priority(3)
                .type("HABIT")
                .title("Complete 5 full interview sessions")
                .description("Consistency matters more than perfection at this stage. "
                    + "Complete one session per day for 5 days across different topics "
                    + "to build interview stamina and see your analytics mature.")
                .metric("Target: 5 completed sessions")
                .icon("🎯")
                .build());
        } else {
            items.add(RoadmapResponse.RoadmapItem.builder()
                .priority(3)
                .type("TOPIC")
                .title("Add HR behavioural practice")
                .description("Technical skills get you the interview — communication "
                    + "gets you the offer. Practice 2 HR sessions using the STAR method. "
                    + "Structure every answer: Situation, Task, Action, Result.")
                .metric("Target: STAR score > 70 in 2 HR sessions")
                .icon("🤝")
                .build());
        }

        return RoadmapResponse.builder()
            .items(items)
            .overallReadiness(level)
            .readinessScore(readiness)
            .build();
    }

    private String findWeakestTopic(Long userId) {
        List<String> topics = List.of("DSA", "JAVA", "SQL", "REACT", "HR");
        String weakest = null;
        double lowestAvg = Double.MAX_VALUE;

        for (String topic : topics) {
            List<InterviewSession> sessions = sessionRepository.findCompletedByUserAndTopic(userId, topic);
            if (sessions.isEmpty()) continue;
            double avg = sessions.stream()
                .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0)
                .average().orElse(0);
            if (avg < lowestAvg) {
                lowestAvg = avg;
                weakest = topic;
            }
        }
        return weakest;
    }

    private String findWeakestDimension(Long userId) {
        double comm = safe(answerRepository.findAverageCommunicationScoreByUserId(userId));
        double prob = safe(answerRepository.findAverageProblemSolvingScoreByUserId(userId));
        double conf = safe(answerRepository.findAverageConfidenceScoreByUserId(userId));
        if (comm == 0 && prob == 0 && conf == 0) return "Communication";
        if (comm <= prob && comm <= conf) return "Communication";
        if (prob <= comm && prob <= conf) return "Problem Solving";
        return "Confidence";
    }

    private String getDimensionAdvice(String dim) {
        return switch (dim) {
            case "Communication" ->
                "Structure your answers clearly. Use the PREP method: "
                + "Point, Reason, Example, Point. Practice speaking your answers "
                + "aloud before typing them — this naturally improves clarity.";
            case "Problem Solving" ->
                "Before answering, state your approach first. "
                + "Think out loud: 'I would approach this by...' "
                + "Examiners reward structured thinking over rushed answers.";
            default ->
                "Write more complete answers. Short answers score low on confidence. "
                + "Aim for 3-5 sentences per response, covering the what, why, and how.";
        };
    }

    private String getDimensionIcon(String dim) {
        return switch (dim) {
            case "Communication"  -> "💬";
            case "Problem Solving"-> "🧠";
            default               -> "💪";
        };
    }

    private double safe(Double d) { return d != null ? d : 0.0; }
}
