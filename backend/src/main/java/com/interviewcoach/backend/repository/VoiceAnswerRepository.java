package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.VoiceAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceAnswerRepository extends JpaRepository<VoiceAnswer, Long> {
}
