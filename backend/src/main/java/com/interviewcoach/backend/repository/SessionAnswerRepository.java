package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.SessionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SessionAnswerRepository extends JpaRepository<SessionAnswer, Long> {

    List<SessionAnswer> findBySessionId(Long sessionId);

    long countBySessionIdAndIsCorrect(Long sessionId, boolean isCorrect);

    // All answers for a session with question eagerly loaded
    @Query("""
        SELECT a FROM SessionAnswer a
        JOIN FETCH a.question q
        WHERE a.session.id = :sessionId
        ORDER BY a.answeredAt ASC
        """)
    List<SessionAnswer> findBySessionIdWithQuestion(@org.springframework.data.repository.query.Param("sessionId") Long sessionId);

    // Average AI score for TEXT answers across all sessions for a user
    @Query("""
        SELECT AVG(a.aiScore)
        FROM SessionAnswer a
        WHERE a.session.user.id = :userId
        AND a.aiEvaluated = true
        """)
    Double findAverageAiScoreByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);

    // Average per-dimension score for a user
    @Query("""
        SELECT AVG(a.communicationScore)
        FROM SessionAnswer a
        WHERE a.session.user.id = :userId
        AND a.communicationScore IS NOT NULL
        """)
    Double findAverageCommunicationScoreByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);

    @Query("""
        SELECT AVG(a.problemSolvingScore)
        FROM SessionAnswer a
        WHERE a.session.user.id = :userId
        AND a.problemSolvingScore IS NOT NULL
        """)
    Double findAverageProblemSolvingScoreByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);

    @Query("""
        SELECT AVG(a.confidenceScore)
        FROM SessionAnswer a
        WHERE a.session.user.id = :userId
        AND a.confidenceScore IS NOT NULL
        """)
    Double findAverageConfidenceScoreByUserId(@org.springframework.data.repository.query.Param("userId") Long userId);

    // Total questions answered across all sessions for a user — for dashboard
    @Query("""
        SELECT COUNT(a) FROM SessionAnswer a
        WHERE a.session.user.id = :userId
        """)
    long countTotalAnsweredByUserId(Long userId);
}