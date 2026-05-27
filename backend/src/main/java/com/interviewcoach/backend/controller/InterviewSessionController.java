package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.*;
import com.interviewcoach.backend.model.User;
import com.interviewcoach.backend.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class InterviewSessionController {

    private final InterviewSessionService sessionService;
    private final AnswerService           answerService;
    private final AnalyticsService        analyticsService;

    // Helper: safely extract userId from JWT principal
    private Long getUserId(UserDetails userDetails) {
        return ((User) userDetails).getId();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
        @Valid @RequestBody CreateSessionRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        SessionResponse response = sessionService.createSession(request, getUserId(userDetails));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.ok(response, "Session created"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getUserSessions(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            sessionService.getUserSessions(getUserId(userDetails)),
            "Sessions fetched"
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SessionResponse>> getSession(
        @PathVariable Long id,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            sessionService.getSessionById(id, getUserId(userDetails)),
            "Session fetched"
        ));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<SessionResponse>> completeSession(
        @PathVariable Long id,
        @RequestBody Map<String, Integer> body,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer duration = body.getOrDefault("durationSeconds", 0);
        return ResponseEntity.ok(ApiResponse.ok(
            sessionService.completeSession(id, getUserId(userDetails), duration),
            "Session completed"
        ));
    }

    @PostMapping("/{sessionId}/answers")
    public ResponseEntity<ApiResponse<AnswerResponse>> submitAnswer(
        @PathVariable Long sessionId,
        @Valid @RequestBody SubmitAnswerRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            answerService.submitAnswer(sessionId, request, getUserId(userDetails)),
            "Answer submitted"
        ));
    }

    @GetMapping("/analytics/summary")
    public ResponseEntity<ApiResponse<AnalyticsSummaryResponse>> getAnalytics(
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            analyticsService.getSummary(getUserId(userDetails)),
            "Analytics fetched"
        ));
    }
}