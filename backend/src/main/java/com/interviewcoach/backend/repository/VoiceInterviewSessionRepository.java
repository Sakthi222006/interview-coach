package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.VoiceInterviewSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoiceInterviewSessionRepository extends JpaRepository<VoiceInterviewSession, Long> {
    Optional<VoiceInterviewSession> findByIdAndUserId(Long id, Long userId);
}
