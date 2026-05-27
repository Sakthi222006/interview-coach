package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.VoiceInterviewResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoiceInterviewResultRepository extends JpaRepository<VoiceInterviewResult, Long> {
    Optional<VoiceInterviewResult> findBySessionId(Long sessionId);
}
