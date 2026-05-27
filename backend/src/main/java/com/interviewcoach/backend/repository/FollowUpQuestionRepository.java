package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.FollowUpQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowUpQuestionRepository extends JpaRepository<FollowUpQuestion, Long> {
    List<FollowUpQuestion> findByScenarioId(Long scenarioId);
    List<FollowUpQuestion> findByParentConversationId(Long parentConversationId);
    List<FollowUpQuestion> findByScenarioIdAndIsAnswered(Long scenarioId, Boolean isAnswered);
}
