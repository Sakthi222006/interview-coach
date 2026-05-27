package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.ApiResponse;
import com.interviewcoach.backend.dto.ResumeQuestionRequest;
import com.interviewcoach.backend.dto.ResumeQuestionResponse;
import com.interviewcoach.backend.service.ResumeQuestionGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
@RequiredArgsConstructor
@Slf4j
public class ResumeQuestionController {

    private final ResumeQuestionGeneratorService resumeQuestionGeneratorService;

    @PostMapping("/{resumeId}/questions")
    public ResponseEntity<ApiResponse<ResumeQuestionResponse>> generateQuestions(
        @PathVariable Long resumeId,
        @RequestBody ResumeQuestionRequest request
    ) {
        try {
            ResumeQuestionResponse response = resumeQuestionGeneratorService.generateQuestions(resumeId, request);
            return ResponseEntity.ok(ApiResponse.ok(response, "Resume interview questions generated"));
        } catch (IllegalArgumentException e) {
            log.warn("Resume question generation bad request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error generating resume questions", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("Failed to generate resume questions"));
        }
    }
}
