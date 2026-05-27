package com.interviewcoach.backend.service;

import com.interviewcoach.backend.dto.*;
import com.interviewcoach.backend.model.*;
import com.interviewcoach.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterSimulationService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final InterviewScenarioRepository scenarioRepository;
    private final InterviewConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;

    // Recruiter profiles with personalities and styles
    private static final Map<String, Map<String, String>> RECRUITER_PROFILES = Map.ofEntries(
        Map.entry("HR_RECRUITER", Map.of(
            "name", "Sarah Johnson",
            "personality", "Warm, empathetic, and professional. Focuses on cultural fit and soft skills.",
            "style", "Conversational, asking about motivation, team collaboration, and work-life balance."
        )),
        Map.entry("SENIOR_DEVELOPER", Map.of(
            "name", "Alex Chen",
            "personality", "Direct, technically rigorous, and detailed. Values clean code and best practices.",
            "style", "Technical deep-dives, code reviews, architecture discussions, and problem-solving approach."
        )),
        Map.entry("TECH_LEAD", Map.of(
            "name", "Marcus Williams",
            "personality", "Strategic, mentoring-focused, and diplomatic. Evaluates leadership potential.",
            "style", "System design discussions, mentoring ability, cross-team collaboration, and vision alignment."
        )),
        Map.entry("ENGINEERING_MANAGER", Map.of(
            "name", "Emily Rodriguez",
            "personality", "People-oriented, pragmatic, and supportive. Focuses on growth and team dynamics.",
            "style", "Leadership style questions, handling conflicts, team growth, and project ownership."
        )),
        Map.entry("SYSTEM_DESIGN_INTERVIEWER", Map.of(
            "name", "David Kumar",
            "personality", "Analytical, thorough, and patient. Deep thinker with passion for scalability.",
            "style", "Large-scale system design, trade-offs, scalability, and real-world constraints."
        ))
    );

    @Transactional
    public RecruiterProfileResponse getOrCreateRecruiterProfile(Long userId, String recruiterType) {
        Optional<RecruiterProfile> existing = recruiterProfileRepository.findByUserIdAndRecruiterType(
            userId, recruiterType
        );

        if (existing.isPresent()) {
            return RecruiterProfileResponse.from(existing.get());
        }

        Map<String, String> profileData = RECRUITER_PROFILES.get(recruiterType);
        if (profileData == null) {
            throw new IllegalArgumentException("Invalid recruiter type: " + recruiterType);
        }

        RecruiterProfile profile = RecruiterProfile.builder()
            .user(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")))
            .recruiterType(recruiterType)
            .recruiterName(profileData.get("name"))
            .personality(profileData.get("personality"))
            .interviewStyle(profileData.get("style"))
            .totalInterviews(0)
            .totalHires(0)
            .averageScore(0.0)
            .build();

        profile = recruiterProfileRepository.save(profile);
        log.info("Created recruiter profile {} for user {}", recruiterType, userId);
        return RecruiterProfileResponse.from(profile);
    }

    @Transactional
    public InterviewScenarioResponse createInterviewScenario(
        Long userId,
        CreateRecruiterScenarioRequest request
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        RecruiterProfile recruiterProfile = recruiterProfileRepository
            .findByUserIdAndRecruiterType(userId, request.getRecruiterType())
            .orElse(null);

        if (recruiterProfile == null) {
            // Create a new recruiter profile for this user
            Map<String, String> profileData = RECRUITER_PROFILES.get(request.getRecruiterType());
            recruiterProfile = RecruiterProfile.builder()
                .user(user)
                .recruiterType(request.getRecruiterType())
                .recruiterName(profileData.get("name"))
                .personality(profileData.get("personality"))
                .interviewStyle(profileData.get("style"))
                .totalInterviews(0)
                .totalHires(0)
                .averageScore(0.0)
                .build();
            recruiterProfile = recruiterProfileRepository.save(recruiterProfile);
        }

        InterviewScenario scenario = InterviewScenario.builder()
            .user(user)
            .recruiterProfile(recruiterProfile)
            .roundType(request.getRoundType())
            .title(request.getTitle())
            .scenarioContext(request.getScenarioContext())
            .jobDescription(request.getJobDescription())
            .totalRounds(request.getTotalRounds() != null ? request.getTotalRounds() : 5)
            .status("IN_PROGRESS")
            .currentRoundIndex(0)
            .technicalScore(0.0)
            .communicationScore(0.0)
            .confidenceScore(0.0)
            .leadershipScore(0.0)
            .problemSolvingScore(0.0)
            .overallScore(0.0)
            .build();

        scenario = scenarioRepository.save(scenario);
        log.info("Created interview scenario {} for user {}", scenario.getId(), userId);
        return InterviewScenarioResponse.from(scenario);
    }

    @Transactional
    public InterviewScenarioResponse getScenarioById(Long scenarioId, Long userId) {
        InterviewScenario scenario = scenarioRepository.findByIdAndUserId(scenarioId, userId)
            .orElseThrow(() -> new AccessDeniedException("Scenario not found or access denied"));
        return InterviewScenarioResponse.from(scenario);
    }

    @Transactional
    public List<InterviewScenarioResponse> getUserScenarios(Long userId) {
        return scenarioRepository.findByUserIdOrderByStartedAtDesc(userId)
            .stream()
            .map(InterviewScenarioResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public InterviewScenarioResponse completeScenario(Long scenarioId, Long userId, Integer durationSeconds) {
        InterviewScenario scenario = scenarioRepository.findByIdAndUserId(scenarioId, userId)
            .orElseThrow(() -> new AccessDeniedException("Scenario not found or access denied"));

        scenario.setStatus("COMPLETED");
        scenario.setCompletedAt(LocalDateTime.now());
        scenario.setDurationSeconds(durationSeconds);

        // Calculate overall score
        double overallScore = (
            scenario.getTechnicalScore() +
            scenario.getCommunicationScore() +
            scenario.getConfidenceScore() +
            scenario.getLeadershipScore() +
            scenario.getProblemSolvingScore()
        ) / 5.0;
        scenario.setOverallScore(Math.round(overallScore * 10.0) / 10.0);

        // Update recruiter profile stats
        RecruiterProfile profile = scenario.getRecruiterProfile();
        profile.setTotalInterviews(profile.getTotalInterviews() + 1);
        if (scenario.getHireRecommendation() != null && scenario.getHireRecommendation().equals("HIRE")) {
            profile.setTotalHires(profile.getTotalHires() + 1);
        }

        double newAverage = (profile.getAverageScore() * (profile.getTotalInterviews() - 1) +
            scenario.getOverallScore()) / profile.getTotalInterviews();
        profile.setAverageScore(Math.round(newAverage * 10.0) / 10.0);
        recruiterProfileRepository.save(profile);

        scenario = scenarioRepository.save(scenario);
        log.info("Completed scenario {} for user {}", scenarioId, userId);
        return InterviewScenarioResponse.from(scenario);
    }
}
