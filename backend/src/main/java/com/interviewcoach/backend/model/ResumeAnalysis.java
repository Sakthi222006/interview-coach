package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resume_analyses", indexes = {
        @Index(name = "idx_analysis_resume", columnList = "resume_id"),
        @Index(name = "idx_analysis_user", columnList = "user_id")
})
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    // Stored as JSON strings for portability
    @Column(name = "skills_json", columnDefinition = "LONGTEXT")
    private String skillsJson;

    @Column(name = "technologies_json", columnDefinition = "LONGTEXT")
    private String technologiesJson;

    @Column(name = "frameworks_json", columnDefinition = "LONGTEXT")
    private String frameworksJson;

    @Column(name = "databases_json", columnDefinition = "LONGTEXT")
    private String databasesJson;

    @Column(name = "tools_json", columnDefinition = "LONGTEXT")
    private String toolsJson;

    @Column(name = "projects_json", columnDefinition = "LONGTEXT")
    private String projectsJson;

    @Column(name = "domains_json", columnDefinition = "LONGTEXT")
    private String domainsJson;

    @Column(name = "strengths_json", columnDefinition = "LONGTEXT")
    private String strengthsJson;

    @Column(name = "weaknesses_json", columnDefinition = "LONGTEXT")
    private String weaknessesJson;

    @Column(name = "recommended_roles_json", columnDefinition = "LONGTEXT")
    private String recommendedRolesJson;

    @Column(name = "resume_score")
    private Integer resumeScore;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "gemini_raw_response", columnDefinition = "LONGTEXT")
    private String geminiRawResponse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        if (this.analyzedAt == null) this.analyzedAt = LocalDateTime.now();
    }
}
