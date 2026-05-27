package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recruiter_profiles", indexes = {
    @Index(name = "idx_recruiter_user", columnList = "user_id"),
    @Index(name = "idx_recruiter_type", columnList = "recruiter_type")
})
public class RecruiterProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // HR_RECRUITER / SENIOR_DEVELOPER / TECH_LEAD / ENGINEERING_MANAGER / SYSTEM_DESIGN_INTERVIEWER
    @Column(nullable = false, length = 30)
    private String recruiterType;

    @Column(length = 100)
    private String recruiterName;  // "Alex Chen", "Sarah Johnson", etc.

    @Column(columnDefinition = "LONGTEXT")
    private String personality;    // Personality traits affecting interview style

    @Column(columnDefinition = "LONGTEXT")
    private String interviewStyle; // Communication style description

    @Column(nullable = false)
    @Builder.Default
    private Integer totalInterviews = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalHires = 0;

    @Column(nullable = false)
    @Builder.Default
    private Double averageScore = 0.0;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
