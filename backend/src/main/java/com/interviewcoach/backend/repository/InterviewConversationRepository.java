package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.InterviewConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InterviewConversationRepository extends JpaRepository<InterviewConversation, Long> {
    List<InterviewConversation> findByScenarioIdOrderByTurnNumberAsc(Long scenarioId);
    Long countByScenarioId(Long scenarioId);
}
