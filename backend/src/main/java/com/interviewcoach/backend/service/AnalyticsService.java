
package com.interviewcoach.backend.service;

import com.interviewcoach.backend.dto.AnalyticsSummaryResponse;
import com.interviewcoach.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final InterviewSessionRepository sessionRepository;
    private final SessionAnswerRepository    answerRepository;
    private final InterviewScenarioRepository scenarioRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;

    public AnalyticsSummaryResponse getSummary(Long userId) {
        long totalSessions     = sessionRepository.countByUserIdAndStatus(userId, "COMPLETED") +
                                 sessionRepository.countByUserIdAndStatus(userId, "IN_PROGRESS");
        long completedSessions = sessionRepository.countByUserIdAndStatus(userId, "COMPLETED");
        Double avgScore        = sessionRepository.findAverageScoreByUserId(userId);
        long totalAnswered     = answerRepository.countTotalAnsweredByUserId(userId);

        // Topic breakdown: how many sessions per topic
        Map<String, Long> byTopic = sessionRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .collect(Collectors.groupingBy(
                s -> s.getTopic(),
                Collectors.counting()
            ));

        // Estimate practice hours from completed sessions
        double practiceHours = sessionRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .mapToInt(s -> s.getDurationSeconds() != null ? s.getDurationSeconds() : 0)
            .sum() / 3600.0;

        return AnalyticsSummaryResponse.builder()
            .totalSessions(totalSessions)
            .completedSessions(completedSessions)
            .averageScore(avgScore != null ? Math.round(avgScore * 10.0) / 10.0 : 0.0)
            .totalQuestionsAnswered(totalAnswered)
            .sessionsByTopic(byTopic)
            .practiceHours(Math.round(practiceHours * 10.0) / 10.0)
            .bestTopic(findTopicByScore(userId, true))
            .weakestTopic(findTopicByScore(userId, false))
            .build();
    }

    private String findTopicByScore(Long userId, boolean best) {
        return sessionRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .filter(s -> s.getScore() != null)
            .collect(Collectors.groupingBy(
                s -> s.getTopic(),
                Collectors.averagingDouble(s -> s.getScore())
            ))
            .entrySet().stream()
            .reduce((a, b) -> best
                ? (a.getValue() >= b.getValue() ? a : b)
                : (a.getValue() <= b.getValue() ? a : b))
            .map(java.util.Map.Entry::getKey)
            .orElse(null);
    }

    // ==================== Recruiter Mode Analytics ====================

    public Map<String, Object> getRecruiterAnalytics(Long userId) {
        long totalRecruiterInterviews = scenarioRepository.countByUserIdAndStatus(userId, "COMPLETED");
        Double avgRecruiterScore = scenarioRepository
            .findByUserIdAndStatusOrderByStartedAtAsc(userId, "COMPLETED")
            .stream()
            .mapToDouble(s -> s.getOverallScore() != null ? s.getOverallScore() : 0.0)
            .average()
            .orElse(0.0);

        // Round breakdown
        Map<String, Long> roundBreakdown = scenarioRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .collect(Collectors.groupingBy(
                s -> s.getRoundType(),
                Collectors.counting()
            ));

        // Recruiter breakdown
        Map<String, Long> recruiterBreakdown = scenarioRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .collect(Collectors.groupingBy(
                s -> s.getRecruiterProfile().getRecruiterType(),
                Collectors.counting()
            ));

        // Hiring recommendations summary
        long hireRecommendations = scenarioRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .filter(s -> "HIRE".equals(s.getHireRecommendation()))
            .count();

        return Map.of(
            "totalRecruiterInterviews", totalRecruiterInterviews,
            "averageRecruiterScore", Math.round(avgRecruiterScore * 10.0) / 10.0,
            "roundBreakdown", roundBreakdown,
            "recruiterBreakdown", recruiterBreakdown,
            "hireRecommendations", hireRecommendations,
            "hireRate", totalRecruiterInterviews > 0 ? 
                Math.round((double) hireRecommendations / totalRecruiterInterviews * 100) : 0
        );
    }

    public Map<String, Object> getRecruiterSummary(Long userId) {
        long totalCompleted = scenarioRepository.countByUserIdAndStatus(userId, "COMPLETED");
        
        if (totalCompleted == 0) {
            return Map.of(
                "recruiterModePracticeHours", 0.0,
                "bestRecruiterRound", "N/A",
                "weakestRecruiterRound", "N/A",
                "totalRecruiterInterviews", 0L
            );
        }

        double practiceHours = scenarioRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .mapToInt(s -> s.getDurationSeconds() != null ? s.getDurationSeconds() : 0)
            .sum() / 3600.0;

        String bestRound = findBestRecruiterRound(userId);
        String weakestRound = findWeakestRecruiterRound(userId);

        return Map.of(
            "recruiterModePracticeHours", Math.round(practiceHours * 10.0) / 10.0,
            "bestRecruiterRound", bestRound != null ? bestRound : "N/A",
            "weakestRecruiterRound", weakestRound != null ? weakestRound : "N/A",
            "totalRecruiterInterviews", totalCompleted
        );
    }

    private String findBestRecruiterRound(Long userId) {
        return scenarioRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .filter(s -> s.getOverallScore() != null)
            .collect(Collectors.groupingBy(
                s -> s.getRoundType(),
                Collectors.averagingDouble(s -> s.getOverallScore())
            ))
            .entrySet().stream()
            .max((a, b) -> Double.compare(a.getValue(), b.getValue()))
            .map(java.util.Map.Entry::getKey)
            .orElse(null);
    }

    private String findWeakestRecruiterRound(Long userId) {
        return scenarioRepository
            .findByUserIdAndStatus(userId, "COMPLETED")
            .stream()
            .filter(s -> s.getOverallScore() != null)
            .collect(Collectors.groupingBy(
                s -> s.getRoundType(),
                Collectors.averagingDouble(s -> s.getOverallScore())
            ))
            .entrySet().stream()
            .min((a, b) -> Double.compare(a.getValue(), b.getValue()))
            .map(java.util.Map.Entry::getKey)
            .orElse(null);
    }
}