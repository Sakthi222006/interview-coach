package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.*;
import com.interviewcoach.backend.model.User;
import com.interviewcoach.backend.model.FollowUpQuestion;
import com.interviewcoach.backend.repository.FollowUpQuestionRepository;
import com.interviewcoach.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterSimulationService recruiterSimulationService;
    private final InterviewCopilotService interviewCopilotService;
    private final FollowUpEngineService followUpEngineService;
    private final FollowUpQuestionRepository followUpQuestionRepository;
    private final AnalyticsService analyticsService;

    private Long getUserId(UserDetails userDetails) {
        return ((User) userDetails).getId();
    }

    // ==================== Recruiter Profile Endpoints ====================

    @GetMapping("/profiles/{recruiterType}")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> getOrCreateProfile(
        @PathVariable String recruiterType,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        RecruiterProfileResponse profile = recruiterSimulationService
            .getOrCreateRecruiterProfile(getUserId(userDetails), recruiterType);
        return ResponseEntity.ok(ApiResponse.ok(profile, "Recruiter profile fetched/created"));
    }

    // ==================== Interview Scenario Endpoints ====================

    @PostMapping("/scenarios")
    public ResponseEntity<ApiResponse<InterviewScenarioResponse>> createScenario(
        @Valid @RequestBody CreateRecruiterScenarioRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        InterviewScenarioResponse response = recruiterSimulationService
            .createInterviewScenario(getUserId(userDetails), request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(response, "Interview scenario created"));
    }

    @GetMapping("/scenarios")
    public ResponseEntity<ApiResponse<List<InterviewScenarioResponse>>> getUserScenarios(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<InterviewScenarioResponse> scenarios = recruiterSimulationService
            .getUserScenarios(getUserId(userDetails));
        return ResponseEntity.ok(ApiResponse.ok(scenarios, "Scenarios fetched"));
    }

    @GetMapping("/scenarios/{scenarioId}")
    public ResponseEntity<ApiResponse<InterviewScenarioResponse>> getScenario(
        @PathVariable Long scenarioId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        InterviewScenarioResponse scenario = recruiterSimulationService
            .getScenarioById(scenarioId, getUserId(userDetails));
        return ResponseEntity.ok(ApiResponse.ok(scenario, "Scenario fetched"));
    }

    @PutMapping("/scenarios/{scenarioId}/complete")
    public ResponseEntity<ApiResponse<InterviewScenarioResponse>> completeScenario(
        @PathVariable Long scenarioId,
        @RequestBody Map<String, Integer> request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer durationSeconds = request.get("durationSeconds");
        InterviewScenarioResponse response = recruiterSimulationService
            .completeScenario(scenarioId, getUserId(userDetails), durationSeconds);
        return ResponseEntity.ok(ApiResponse.ok(response, "Scenario completed"));
    }

    // ==================== Interview Flow Endpoints ====================

    @GetMapping("/scenarios/{scenarioId}/next-question")
    public ResponseEntity<ApiResponse<Map<String, String>>> getNextQuestion(
        @PathVariable Long scenarioId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        String question = interviewCopilotService.getNextQuestion(scenarioId, getUserId(userDetails));
        if (question == null) {
            return ResponseEntity.ok(ApiResponse.ok(
                Map.of("question", "Interview completed"),
                "Interview completed"
            ));
        }
        return ResponseEntity.ok(ApiResponse.ok(
            Map.of("question", question),
            "Next question retrieved"
        ));
    }

    @PostMapping("/scenarios/{scenarioId}/submit-answer")
    public ResponseEntity<ApiResponse<InterviewConversationResponse>> submitAnswer(
        @PathVariable Long scenarioId,
        @Valid @RequestBody CandidateAnswerRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        InterviewConversationResponse response = interviewCopilotService
            .processAnswer(scenarioId, getUserId(userDetails), request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(response, "Answer submitted and evaluated"));
    }

    // ==================== Conversation History Endpoints ====================

    @GetMapping("/scenarios/{scenarioId}/conversation")
    public ResponseEntity<ApiResponse<List<InterviewConversationResponse>>> getConversationHistory(
        @PathVariable Long scenarioId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<InterviewConversationResponse> history = interviewCopilotService
            .getConversationHistory(scenarioId, getUserId(userDetails));
        return ResponseEntity.ok(ApiResponse.ok(history, "Conversation history retrieved"));
    }

    // ==================== Summary & Feedback Endpoints ====================

    @GetMapping("/scenarios/{scenarioId}/summary")
    public ResponseEntity<ApiResponse<InterviewSummaryResponse>> getInterviewSummary(
        @PathVariable Long scenarioId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        InterviewSummaryResponse summary = interviewCopilotService
            .getInterviewSummary(scenarioId, getUserId(userDetails));
        return ResponseEntity.ok(ApiResponse.ok(summary, "Interview summary generated"));
    }

    @GetMapping("/scenarios/{scenarioId}/follow-ups")
    public ResponseEntity<ApiResponse<?>> getFollowUpQuestions(
        @PathVariable Long scenarioId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Verify user has access
        recruiterSimulationService.getScenarioById(scenarioId, getUserId(userDetails));

        // Get follow-up questions for this scenario
        List<FollowUpQuestion> followUps = followUpQuestionRepository.findByScenarioId(scenarioId);
        return ResponseEntity.ok(ApiResponse.ok(
            followUps.stream().map(FollowUpQuestionResponse::from).toList(),
            "Follow-up questions retrieved"
        ));
    }
}
