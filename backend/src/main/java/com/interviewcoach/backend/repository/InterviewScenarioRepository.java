package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.InterviewScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewScenarioRepository extends JpaRepository<InterviewScenario, Long> {
    List<InterviewScenario> findByUserIdOrderByStartedAtDesc(Long userId);
    List<InterviewScenario> findByUserIdAndStatus(Long userId, String status);
    List<InterviewScenario> findByUserIdAndStatusOrderByStartedAtAsc(Long userId, String status);
    Optional<InterviewScenario> findByIdAndUserId(Long id, Long userId);
    List<InterviewScenario> findTop5ByUserIdAndStatusOrderByStartedAtDesc(Long userId, String status);
    
    @Query("SELECT COUNT(s) FROM InterviewScenario s WHERE s.user.id = :userId AND s.status = :status")
    long countByUserIdAndStatus(Long userId, String status);
}

