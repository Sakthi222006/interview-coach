package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Long> {
    List<RecruiterProfile> findByUserId(Long userId);
    Optional<RecruiterProfile> findByUserIdAndRecruiterType(Long userId, String recruiterType);
}
