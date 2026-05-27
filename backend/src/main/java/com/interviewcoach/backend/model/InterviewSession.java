package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interview_sessions", indexes = {
    @Index(name = "idx_session_user", columnList = "user_id"),
    @Index(name = "idx_session_status", columnList = "status")
})
public class InterviewSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Which user owns this session — enforced at service layer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String topic;        // DSA / JAVA / SQL / REACT / HR

    @Column(nullable = false, length = 20)
    private String difficulty;   // EASY / MEDIUM / HARD

    // IN_PROGRESS / COMPLETED / ABANDONED
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "IN_PROGRESS";

    @Column(name = "total_questions")
    @Builder.Default
    private Integer totalQuestions = 0;

    @Column(name = "answered_questions")
    @Builder.Default
    private Integer answeredQuestions = 0;

    // Final score as percentage: 85.5 means 85.5%
    @Builder.Default
    private Double score = 0.0;

    @Column(name = "started_at")
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    // One session has many answers
    // CascadeType.ALL = if session deleted, delete its answers too
    // orphanRemoval = if answer removed from list, delete from DB
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SessionAnswer> answers = new ArrayList<>();
}