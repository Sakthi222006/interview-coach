package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.*;
import com.interviewcoach.backend.model.User;
import com.interviewcoach.backend.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    private Long getUserId(UserDetails userDetails) {
        return ((User) userDetails).getId();
    }

    @GetMapping("/profiles")
    public ResponseEntity<ApiResponse<List<CompanyProfileResponse>>> getProfiles() {
        return ResponseEntity.ok(ApiResponse.ok(
                companyService.getAllProfiles(),
                "Company profiles fetched"
        ));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CompanyProfileResponse>> getProfile(
            @RequestParam String companyName
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                companyService.getProfileByCompanyName(companyName),
                "Company profile fetched"
        ));
    }

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<List<AptitudeQuestionResponse>>> getQuestions(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "10") int limit
    ) {
        if (companyId != null) {
            return ResponseEntity.ok(ApiResponse.ok(
                    companyService.getQuestions(companyId, category, difficulty, limit),
                    "Company aptitude questions fetched"
            ));
        }
        if (companyName != null && !companyName.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(
                    companyService.getQuestionsByCompanyName(companyName, category, difficulty, limit),
                    "Company aptitude questions fetched"
            ));
        }
        throw new IllegalArgumentException("companyId or companyName is required");
    }

    @GetMapping("/coding")
    public ResponseEntity<ApiResponse<List<CodingChallengeResponse>>> getCodingChallenges(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String topic
    ) {
        if (companyId != null) {
            return ResponseEntity.ok(ApiResponse.ok(
                    companyService.getCodingChallenges(companyId, difficulty, topic),
                    "Company coding challenges fetched"
            ));
        }
        if (companyName != null && !companyName.isBlank()) {
            return ResponseEntity.ok(ApiResponse.ok(
                    companyService.getCodingChallengesByCompanyName(companyName, difficulty, topic),
                    "Company coding challenges fetched"
            ));
        }
        throw new IllegalArgumentException("companyId or companyName is required");
    }

    @PostMapping("/coding/{challengeId}/submit")
    public ResponseEntity<ApiResponse<CodingResultResponse>> submitCodingSolution(
            @PathVariable Long challengeId,
            @Valid @RequestBody SubmitCodingSolutionRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CodingResultResponse response = companyService.submitCodingSolution(challengeId, request, (User) userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Coding assessment result generated"));
    }

    @GetMapping("/mock-interview")
    public ResponseEntity<ApiResponse<CompanyMockInterviewResponse>> getMockInterview(
            @RequestParam Long companyId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                companyService.getMockInterview(companyId),
                "Company mock interview generated"
        ));
    }

    @GetMapping("/readiness")
    public ResponseEntity<ApiResponse<CompanyReadinessResponse>> getReadiness(
            @RequestParam(required = false) Long companyId,
            @RequestParam(required = false) String companyName,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CompanyReadinessResponse response;
        if (companyId != null) {
            response = companyService.getReadiness(companyId, getUserId(userDetails));
        } else if (companyName != null && !companyName.isBlank()) {
            response = companyService.getReadinessByCompanyName(companyName, getUserId(userDetails));
        } else {
            throw new IllegalArgumentException("companyId or companyName is required");
        }
        return ResponseEntity.ok(ApiResponse.ok(response, "Company readiness calculated"));
    }
}
