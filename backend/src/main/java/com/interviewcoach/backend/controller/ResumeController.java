package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.ApiResponse;
import com.interviewcoach.backend.dto.ResumeAnalysisHistoryResponse;
import com.interviewcoach.backend.dto.ResumeExtractionResult;
import com.interviewcoach.backend.dto.ResumeMatchRequest;
import com.interviewcoach.backend.dto.ResumeMatchResponse;
import com.interviewcoach.backend.dto.ResumeReadinessResponse;
import com.interviewcoach.backend.dto.ResumeUploadResponse;
import com.interviewcoach.backend.service.ResumeIntelligenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeController {

    private final ResumeIntelligenceService resumeIntelligenceService;

    private Long getUserId(UserDetails userDetails) {
        return ((com.interviewcoach.backend.model.User) userDetails).getId();
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ResumeUploadResponse>> uploadResume(
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            ResumeUploadResponse response = resumeIntelligenceService.uploadResume(file, getUserId(userDetails));
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Resume uploaded successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Resume upload bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to upload resume", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to upload resume"));
        }
    }

    @PostMapping("/{resumeId}/analyze")
    public ResponseEntity<ApiResponse<ResumeExtractionResult>> analyzeResume(
        @PathVariable Long resumeId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            ResumeExtractionResult result = resumeIntelligenceService.analyzeResume(resumeId, getUserId(userDetails));
            return ResponseEntity.ok(ApiResponse.ok(result, "Resume analysis completed"));
        } catch (IllegalArgumentException e) {
            log.warn("Resume analyze bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to analyze resume", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to analyze resume"));
        }
    }

    @GetMapping("/{resumeId}/analysis")
    public ResponseEntity<ApiResponse<ResumeExtractionResult>> getLatestAnalysis(
        @PathVariable Long resumeId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        return resumeIntelligenceService.getLatestAnalysis(resumeId, getUserId(userDetails))
            .map(response -> ResponseEntity.ok(ApiResponse.ok(response, "Latest resume analysis fetched")))
            .orElseGet(() -> ResponseEntity.ok(ApiResponse.ok(null, "No analysis found for this resume")));
    }

    @GetMapping("/{resumeId}/history")
    public ResponseEntity<ApiResponse<ResumeAnalysisHistoryResponse>> getAnalysisHistory(
        @PathVariable Long resumeId,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        ResumeAnalysisHistoryResponse history = resumeIntelligenceService.getAnalysisHistory(resumeId, getUserId(userDetails));
        return ResponseEntity.ok(ApiResponse.ok(history, "Resume analysis history fetched"));
    }

    @PostMapping("/{resumeId}/match")
    public ResponseEntity<ApiResponse<ResumeMatchResponse>> matchResume(
        @PathVariable Long resumeId,
        @RequestBody ResumeMatchRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            ResumeMatchResponse response = resumeIntelligenceService.matchResume(resumeId, request, getUserId(userDetails));
            return ResponseEntity.ok(ApiResponse.ok(response, "Resume match generated"));
        } catch (IllegalArgumentException e) {
            log.warn("Resume match bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error generating resume match", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to generate resume match"));
        }
    }

    @GetMapping("/{resumeId}/readiness")
    public ResponseEntity<ApiResponse<ResumeReadinessResponse>> getReadiness(
        @PathVariable Long resumeId,
        @RequestParam(required = false) String targetRole,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            ResumeReadinessResponse response = resumeIntelligenceService.getReadiness(resumeId, getUserId(userDetails), targetRole);
            return ResponseEntity.ok(ApiResponse.ok(response, "Resume readiness calculated"));
        } catch (IllegalArgumentException e) {
            log.warn("Resume readiness bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error computing readiness", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to compute readiness"));
        }
    }
}
