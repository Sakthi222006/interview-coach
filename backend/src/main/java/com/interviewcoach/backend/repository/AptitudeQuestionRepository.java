package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.AptitudeQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AptitudeQuestionRepository extends JpaRepository<AptitudeQuestion, Long> {
    List<AptitudeQuestion> findByCompanyId(Long companyId);
    List<AptitudeQuestion> findByCompanyIdAndCategory(Long companyId, AptitudeQuestion.Category category);
    List<AptitudeQuestion> findByCompanyIdAndDifficulty(Long companyId, com.interviewcoach.backend.model.CompanyProfile.DifficultyLevel difficulty);
    List<AptitudeQuestion> findByCompanyIdAndCategoryAndDifficulty(Long companyId, AptitudeQuestion.Category category, com.interviewcoach.backend.model.CompanyProfile.DifficultyLevel difficulty);
}
