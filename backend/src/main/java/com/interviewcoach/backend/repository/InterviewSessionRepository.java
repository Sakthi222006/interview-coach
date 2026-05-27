package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.InterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {

    // All sessions for a user, newest first — for history page
    List<InterviewSession> findByUserIdOrderByStartedAtDesc(Long userId);

    // For analytics: only completed sessions
    List<InterviewSession> findByUserIdAndStatus(Long userId, String status);

    // Last N completed sessions for a user on a specific topic
    List<InterviewSession> findTop3ByUserIdAndTopicAndStatusOrderByStartedAtDesc(
        Long userId, String topic, String status
    );

    // All completed sessions ordered by date for trend calculation
    List<InterviewSession> findByUserIdAndStatusOrderByStartedAtAsc(Long userId, String status);

    // Sessions grouped by topic for radar calculation — fetch all completed
    @Query("""
        SELECT s FROM InterviewSession s
        WHERE s.user.id = :userId
        AND s.status = 'COMPLETED'
        AND s.topic = :topic
        ORDER BY s.startedAt DESC
        """)
    List<InterviewSession> findCompletedByUserAndTopic(
        @org.springframework.data.repository.query.Param("userId") Long userId,
        @org.springframework.data.repository.query.Param("topic")  String topic
    );

    // Security check — only return session if it belongs to this user
    Optional<InterviewSession> findByIdAndUserId(Long id, Long userId);

    // Dashboard stat: count completed interviews
    long countByUserIdAndStatus(Long userId, String status);

    // Average score across all completed sessions
    @Query("""
        SELECT AVG(s.score) FROM InterviewSession s
        WHERE s.user.id = :userId AND s.status = 'COMPLETED'
        """)
    Double findAverageScoreByUserId(Long userId);

    // Recent 5 sessions for dashboard widget
    List<InterviewSession> findTop5ByUserIdOrderByStartedAtDesc(Long userId);
}