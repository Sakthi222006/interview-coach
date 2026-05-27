package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "questions", indexes = {
    // Indexes make queries like "get all DSA MEDIUM questions" fast
    @Index(name = "idx_topic",      columnList = "topic"),
    @Index(name = "idx_difficulty", columnList = "difficulty"),
    @Index(name = "idx_topic_diff", columnList = "topic, difficulty")
})
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // DSA / JAVA / SQL / REACT / HR
    @Column(nullable = false, length = 50)
    private String topic;

    // EASY / MEDIUM / HARD
    @Column(nullable = false, length = 20)
    private String difficulty;

    // The actual question text — TEXT allows up to 65,535 characters
    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    // MCQ options — null for TEXT/CODING type questions
    @Column(length = 500)
    private String optionA;

    @Column(length = 500)
    private String optionB;

    @Column(length = 500)
    private String optionC;

    @Column(length = 500)
    private String optionD;

    // "A", "B", "C", or "D" for MCQ — null for text questions
    @Column(length = 10)
    private String correctAnswer;

    // Shown after answering — explains why the answer is correct
    @Column(columnDefinition = "TEXT")
    private String explanation;

    // MCQ / TEXT (short written answer) / CODING
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String questionType = "MCQ";

    // Comma-separated: "arrays,binary-search,two-pointers"
    @Column(length = 255)
    private String tags;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}