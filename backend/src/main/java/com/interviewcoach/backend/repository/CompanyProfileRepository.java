package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.CompanyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyProfileRepository extends JpaRepository<CompanyProfile, Long> {
    Optional<CompanyProfile> findByCompanyNameIgnoreCase(String companyName);
    List<CompanyProfile> findAllByOrderByCompanyNameAsc();
}
