package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.CodingSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodingSubmissionRepository extends JpaRepository<CodingSubmission, Long> {
    List<CodingSubmission> findByUserIdOrderBySubmittedAtDesc(Long userId);
}
