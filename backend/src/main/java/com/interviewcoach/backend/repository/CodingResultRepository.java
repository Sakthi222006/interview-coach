package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.CodingResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodingResultRepository extends JpaRepository<CodingResult, Long> {
    Optional<CodingResult> findTopBySubmissionUserIdOrderByCreatedAtDesc(Long userId);
}
