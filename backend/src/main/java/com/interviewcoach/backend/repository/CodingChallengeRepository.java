package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.CodingChallenge;
import com.interviewcoach.backend.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodingChallengeRepository extends JpaRepository<CodingChallenge, Long> {
    List<CodingChallenge> findByCompanyId(Long companyId);
    List<CodingChallenge> findByCompanyIdAndDifficulty(Long companyId, CompanyProfile.DifficultyLevel difficulty);
    List<CodingChallenge> findByCompanyIdAndTopicIgnoreCase(Long companyId, String topic);
}
