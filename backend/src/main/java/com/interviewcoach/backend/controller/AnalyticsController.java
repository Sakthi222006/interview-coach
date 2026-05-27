package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.AdaptiveRecommendationResponse;
import com.interviewcoach.backend.dto.ApiResponse;
import com.interviewcoach.backend.dto.PerformanceTrendResponse;
import com.interviewcoach.backend.dto.RoadmapResponse;
import com.interviewcoach.backend.dto.SessionReviewResponse;
import com.interviewcoach.backend.dto.SkillRadarResponse;
import com.interviewcoach.backend.dto.TopicAnalysisResponse;
import com.interviewcoach.backend.model.User;
import com.interviewcoach.backend.service.AdaptiveEngineService;
import com.interviewcoach.backend.service.PerformanceAnalyticsService;
import com.interviewcoach.backend.service.RoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final PerformanceAnalyticsService analyticsService;
    private final AdaptiveEngineService       adaptiveEngineService;
    private final RoadmapService              roadmapService;

    private Long uid(UserDetails u) { return ((User) u).getId(); }

    @GetMapping("/trend")
    public ResponseEntity<ApiResponse<PerformanceTrendResponse>> getTrend(
            @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(ApiResponse.ok(
            analyticsService.getTrend(uid(u)), "Trend data fetched"));
    }

    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<TopicAnalysisResponse>> getTopics(
            @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(ApiResponse.ok(
            analyticsService.getTopicAnalysis(uid(u)), "Topic analysis fetched"));
    }

    @GetMapping("/radar")
    public ResponseEntity<ApiResponse<SkillRadarResponse>> getRadar(
            @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(ApiResponse.ok(
            analyticsService.getSkillRadar(uid(u)), "Skill radar fetched"));
    }

    @GetMapping("/roadmap")
    public ResponseEntity<ApiResponse<RoadmapResponse>> getRoadmap(
            @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(ApiResponse.ok(
            roadmapService.generateRoadmap(uid(u)), "Roadmap generated"));
    }

    @GetMapping("/adaptive")
    public ResponseEntity<ApiResponse<AdaptiveRecommendationResponse>> getAdaptive(
            @RequestParam String topic,
            @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(ApiResponse.ok(
            adaptiveEngineService.recommend(uid(u), topic), "Recommendation ready"));
    }

    @GetMapping("/sessions/{id}/review")
    public ResponseEntity<ApiResponse<SessionReviewResponse>> getReview(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails u) {
        return ResponseEntity.ok(ApiResponse.ok(
            analyticsService.getSessionReview(id, uid(u)), "Review fetched"));
    }
}
