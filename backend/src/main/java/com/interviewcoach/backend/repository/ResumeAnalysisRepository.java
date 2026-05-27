package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    Optional<ResumeAnalysis> findTopByResumeIdOrderByAnalyzedAtDesc(Long resumeId);
    Optional<ResumeAnalysis> findTopByUserIdOrderByAnalyzedAtDesc(Long userId);
    List<ResumeAnalysis> findByResumeIdOrderByAnalyzedAtDesc(Long resumeId);
}
