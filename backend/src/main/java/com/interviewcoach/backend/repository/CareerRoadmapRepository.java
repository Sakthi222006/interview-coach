package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.CareerRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareerRoadmapRepository extends JpaRepository<CareerRoadmap, Long> {
    List<CareerRoadmap> findByUserIdOrderByIdDesc(Long userId);
}
