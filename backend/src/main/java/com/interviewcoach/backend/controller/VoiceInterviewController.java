package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.ApiResponse;
import com.interviewcoach.backend.dto.VoiceAnswerResponse;
import com.interviewcoach.backend.dto.VoiceInterviewStartRequest;
import com.interviewcoach.backend.dto.VoiceSessionResponse;
import com.interviewcoach.backend.dto.VoiceTranscriptRequest;
import com.interviewcoach.backend.dto.VoiceSessionSummaryResponse;
import com.interviewcoach.backend.service.VoiceInterviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/voice/interview")
@RequiredArgsConstructor
@Slf4j
public class VoiceInterviewController {

    private final VoiceInterviewService voiceInterviewService;

    private Long getUserId(UserDetails userDetails) {
        return ((com.interviewcoach.backend.model.User) userDetails).getId();
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<VoiceSessionResponse>> startInterview(
        @Valid @RequestBody VoiceInterviewStartRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            VoiceSessionResponse response = voiceInterviewService.startSession(request, getUserId(userDetails));
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Voice interview session started"));
        } catch (Exception e) {
            log.error("Failed to start voice interview", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Unable to start voice interview"));
        }
    }

    @PostMapping("/{sessionId}/transcript")
    public ResponseEntity<ApiResponse<VoiceAnswerResponse>> submitTranscript(
        @PathVariable Long sessionId,
        @Valid @RequestBody VoiceTranscriptRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            VoiceAnswerResponse response = voiceInterviewService.submitTranscript(sessionId, request, getUserId(userDetails));
            return ResponseEntity.ok(ApiResponse.ok(response, "Transcript analyzed"));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid voice transcript request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to analyze voice transcript", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Unable to analyze transcript"));
        }
    }

    @PostMapping("/{sessionId}/stop")
    public ResponseEntity<ApiResponse<VoiceSessionResponse>> stopInterview(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            VoiceSessionResponse response = voiceInterviewService.stopSession(sessionId, getUserId(userDetails));
            return ResponseEntity.ok(ApiResponse.ok(response, "Voice interview session completed"));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid stop request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to stop voice interview session", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Unable to complete voice interview"));
        }
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<VoiceSessionSummaryResponse>> getInterview(
        @PathVariable Long sessionId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            VoiceSessionSummaryResponse response = voiceInterviewService.getSessionSummary(sessionId, getUserId(userDetails));
            return ResponseEntity.ok(ApiResponse.ok(response, "Voice interview session fetched"));
        } catch (Exception e) {
            log.error("Failed to fetch voice interview session", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Unable to fetch voice interview session"));
        }
    }
}
