package com.interviewcoach.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.dto.PerformanceTrendResponse;
import com.interviewcoach.backend.dto.SessionReviewResponse;
import com.interviewcoach.backend.dto.SkillRadarResponse;
import com.interviewcoach.backend.dto.TopicAnalysisResponse;
import com.interviewcoach.backend.model.InterviewSession;
import com.interviewcoach.backend.model.SessionAnswer;
import com.interviewcoach.backend.repository.InterviewSessionRepository;
import com.interviewcoach.backend.repository.SessionAnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceAnalyticsService {

    private final InterviewSessionRepository sessionRepository;
    private final SessionAnswerRepository    answerRepository;

    public PerformanceTrendResponse getTrend(Long userId) {
        List<InterviewSession> sessions =
            sessionRepository.findByUserIdAndStatusOrderByStartedAtAsc(userId, "COMPLETED");

        if (sessions.isEmpty()) {
            return PerformanceTrendResponse.builder()
                .dataPoints(List.of())
                .trajectory("INSUFFICIENT")
                .improvementDelta(0.0)
                .build();
        }

        List<PerformanceTrendResponse.TrendPoint> points = sessions.stream()
            .map(s -> {
                Double avgAiScore = answerRepository.findBySessionIdWithQuestion(s.getId())
                    .stream()
                    .filter(a -> Boolean.TRUE.equals(a.getAiEvaluated()) && a.getAiScore() != null)
                    .mapToInt(SessionAnswer::getAiScore)
                    .average()
                    .orElse(0.0);

                return PerformanceTrendResponse.TrendPoint.builder()
                    .sessionId(s.getId())
                    .topic(s.getTopic())
                    .difficulty(s.getDifficulty())
                    .score(s.getScore())
                    .aiOverallScore(avgAiScore > 0 ? (int) Math.round(avgAiScore) : null)
                    .completedAt(s.getCompletedAt())
                    .durationSeconds(s.getDurationSeconds())
                    .questionCount(s.getTotalQuestions())
                    .build();
            })
            .collect(Collectors.toList());

        String trajectory = computeTrajectory(sessions);
        double delta = sessions.size() >= 2
            ? sessions.get(sessions.size()-1).getScore() - sessions.get(0).getScore()
            : 0.0;

        return PerformanceTrendResponse.builder()
            .dataPoints(points)
            .trajectory(trajectory)
            .improvementDelta(Math.round(delta * 10.0) / 10.0)
            .build();
    }

    private String computeTrajectory(List<InterviewSession> sessions) {
        if (sessions.size() < 3) return "INSUFFICIENT";
        int n = sessions.size();
        double firstAvg = sessions.subList(0, Math.min(3, n)).stream()
            .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0).average().orElse(0);
        double lastAvg  = sessions.subList(Math.max(0, n-3), n).stream()
            .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0).average().orElse(0);
        if (lastAvg - firstAvg >= 10) return "IMPROVING";
        if (firstAvg - lastAvg >= 10) return "DECLINING";
        return "PLATEAUING";
    }

    public TopicAnalysisResponse getTopicAnalysis(Long userId) {
        List<String> allTopics = List.of("DSA", "JAVA", "SQL", "REACT", "HR");
        List<TopicAnalysisResponse.TopicStat> stats = new ArrayList<>();

        for (String topic : allTopics) {
            List<InterviewSession> completed =
                sessionRepository.findCompletedByUserAndTopic(userId, topic);

            if (completed.isEmpty()) {
                stats.add(TopicAnalysisResponse.TopicStat.builder()
                    .topic(topic)
                    .sessionsCompleted(0L)
                    .averageScore(0.0)
                    .rollingAverage(0.0)
                    .bestScore(0.0)
                    .worstScore(0.0)
                    .trend("INSUFFICIENT")
                    .recommendedDifficulty("EASY")
                    .build());
                continue;
            }

            double avg = completed.stream()
                .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0)
                .average().orElse(0);

            List<InterviewSession> last3 = completed.subList(0, Math.min(3, completed.size()));
            double rolling = last3.stream()
                .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0)
                .average().orElse(0);

            double best  = completed.stream().mapToDouble(s -> s.getScore() != null ? s.getScore() : 0).max().orElse(0);
            double worst = completed.stream().mapToDouble(s -> s.getScore() != null ? s.getScore() : 0).min().orElse(0);

            String trend = computeTrajectory(new ArrayList<>(
                completed.subList(Math.max(0, completed.size()-5), completed.size())
            ));

            String difficulty = rolling >= 75 ? "HARD" : rolling >= 50 ? "MEDIUM" : "EASY";

            stats.add(TopicAnalysisResponse.TopicStat.builder()
                .topic(topic)
                .sessionsCompleted((long) completed.size())
                .averageScore(Math.round(avg * 10.0) / 10.0)
                .rollingAverage(Math.round(rolling * 10.0) / 10.0)
                .bestScore(best)
                .worstScore(worst)
                .trend(trend)
                .recommendedDifficulty(difficulty)
                .build());
        }

        List<String> weak = stats.stream()
            .filter(s -> s.getSessionsCompleted() >= 1 && s.getRollingAverage() < 60)
            .map(TopicAnalysisResponse.TopicStat::getTopic)
            .collect(Collectors.toList());

        List<String> strong = stats.stream()
            .filter(s -> s.getSessionsCompleted() >= 1 && s.getRollingAverage() >= 75)
            .map(TopicAnalysisResponse.TopicStat::getTopic)
            .collect(Collectors.toList());

        return TopicAnalysisResponse.builder()
            .topicStats(stats)
            .weakTopics(weak)
            .strongTopics(strong)
            .build();
    }

    public SkillRadarResponse getSkillRadar(Long userId) {
        return SkillRadarResponse.builder()
            .dsaProficiency(  topicScore(userId, "DSA"))
            .javaProficiency( topicScore(userId, "JAVA"))
            .sqlProficiency(  topicScore(userId, "SQL"))
            .reactProficiency(topicScore(userId, "REACT"))
            .hrScore(         topicScore(userId, "HR"))
            .communicationScore(safeInt(answerRepository.findAverageCommunicationScoreByUserId(userId)))
            .problemSolvingScore(safeInt(answerRepository.findAverageProblemSolvingScoreByUserId(userId)))
            .confidenceScore(safeInt(answerRepository.findAverageConfidenceScoreByUserId(userId)))
            .build();
    }

    private int topicScore(Long userId, String topic) {
        List<InterviewSession> sessions =
            sessionRepository.findCompletedByUserAndTopic(userId, topic);
        if (sessions.isEmpty()) return 0;
        return (int) Math.round(sessions.stream()
            .mapToDouble(s -> s.getScore() != null ? s.getScore() : 0)
            .average().orElse(0));
    }

    public SessionReviewResponse getSessionReview(Long sessionId, Long userId) {
        InterviewSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new RuntimeException("Session not found"));

        List<SessionAnswer> answers =
            answerRepository.findBySessionIdWithQuestion(sessionId);

        List<SessionReviewResponse.AnswerReview> reviews = new ArrayList<>();
        int qNum = 1;
        for (SessionAnswer a : answers) {
            reviews.add(SessionReviewResponse.AnswerReview.builder()
                .questionNumber(qNum++)
                .questionText(a.getQuestion().getQuestionText())
                .questionType(a.getQuestion().getQuestionType())
                .difficulty(a.getQuestion().getDifficulty())
                .userAnswer(a.getUserAnswer())
                .isCorrect(Boolean.TRUE.equals(a.getIsCorrect()))
                .correctAnswer(a.getQuestion().getCorrectAnswer())
                .explanation(a.getQuestion().getExplanation())
                .timeSpentSeconds(a.getTimeSpentSeconds())
                .aiOverallScore(a.getAiScore())
                .technicalScore(a.getTechnicalScore())
                .communicationScore(a.getCommunicationScore())
                .confidenceScore(a.getConfidenceScore() != null ? (int) Math.round(a.getConfidenceScore()) : null)
                .interviewerFeedback(a.getInterviewerFeedback())
                .strengths(parseJson(a.getStrengthsJson()))
                .improvements(parseJson(a.getImprovementsJson()))
                .starTotalScore(a.getStarTotalScore())
                .starSituationScore(a.getStarSituationScore())
                .starTaskScore(a.getStarTaskScore())
                .starActionScore(a.getStarActionScore())
                .starResultScore(a.getStarResultScore())
                .build());
        }

        return SessionReviewResponse.builder()
            .sessionId(session.getId())
            .topic(session.getTopic())
            .difficulty(session.getDifficulty())
            .finalScore(session.getScore())
            .durationSeconds(session.getDurationSeconds())
            .answers(reviews)
            .build();
    }

    @SuppressWarnings("unchecked")
    private List<String> parseJson(String json) {
        if (json == null || json.isBlank()) return List.of();
        try {
            return new ObjectMapper().readValue(json, List.class);
        } catch (Exception e) {
            return List.of();
        }
    }

    private int safeInt(Double d) {
        return d != null ? (int) Math.round(d) : 0;
    }
}
