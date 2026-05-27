
package com.interviewcoach.backend.service;

import com.interviewcoach.backend.dto.*;
import com.interviewcoach.backend.model.*;
import com.interviewcoach.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InterviewSessionService {

    private final InterviewSessionRepository sessionRepository;
    private final QuestionRepository         questionRepository;
    private final UserRepository             userRepository;

    @Transactional
    public SessionResponse createSession(CreateSessionRequest req, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Fetch random questions for this topic + difficulty
        List<Question> questions = questionRepository.findRandomByTopicAndDifficulty(
            req.getTopic().toUpperCase(),
            req.getDifficulty().toUpperCase(),
            req.getTotalQuestions()
        );

        if (questions.isEmpty()) {
            throw new RuntimeException(
                "No questions found for topic: " + req.getTopic() +
                ", difficulty: " + req.getDifficulty()
            );
        }

        // 2. Create and save the session
        InterviewSession session = InterviewSession.builder()
            .user(user)
            .topic(req.getTopic().toUpperCase())
            .difficulty(req.getDifficulty().toUpperCase())
            .totalQuestions(questions.size())
            .status("IN_PROGRESS")
            .build();

        session = sessionRepository.save(session);
        log.info("Created session {} for user {}", session.getId(), userId);

        // 3. Return session WITH questions (frontend needs them to show)
        SessionResponse response = SessionResponse.from(session);
        response.setQuestions(questions.stream().map(QuestionResponse::from).toList());
        return response;
    }

    public List<SessionResponse> getUserSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByStartedAtDesc(userId)
            .stream()
            .map(SessionResponse::from)
            .toList();
    }

    public SessionResponse getSessionById(Long sessionId, Long userId) {
        // Security: ensure session belongs to requesting user
        InterviewSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new AccessDeniedException("Session not found or access denied"));

        return SessionResponse.from(session);
    }

    @Transactional
    public SessionResponse completeSession(Long sessionId, Long userId, Integer durationSeconds) {
        InterviewSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
            .orElseThrow(() -> new AccessDeniedException("Session not found or access denied"));

        // Calculate score: correct answers / total questions * 100
        long correctCount = session.getAnswers().stream()
            .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
            .count();

        double score = session.getTotalQuestions() > 0
            ? (double) correctCount / session.getTotalQuestions() * 100
            : 0.0;

        session.setStatus("COMPLETED");
        session.setScore(Math.round(score * 10.0) / 10.0); // round to 1 decimal
        session.setCompletedAt(LocalDateTime.now());
        session.setDurationSeconds(durationSeconds);

        session = sessionRepository.save(session);
        log.info("Completed session {} with score {}%", sessionId, session.getScore());

        return SessionResponse.from(session);
    }
}