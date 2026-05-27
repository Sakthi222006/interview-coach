package com.interviewcoach.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.dto.CompanyMockInterviewResponse;
import com.interviewcoach.backend.model.CompanyProfile;
import com.interviewcoach.backend.repository.CompanyProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyInterviewService {

    private final CompanyProfileRepository companyProfileRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CompanyMockInterviewResponse generateMockInterview(Long companyId) {
        CompanyProfile profile = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        List<String> rounds = parseInterviewRounds(profile.getInterviewRounds());
        List<CompanyMockInterviewResponse.InterviewRound> interviewRounds = new ArrayList<>();

        int index = 1;
        for (String round : rounds) {
            interviewRounds.add(CompanyMockInterviewResponse.InterviewRound.builder()
                    .title("Round " + index + ": " + round)
                    .description(buildRoundDescription(profile, round))
                    .focusAreas(buildFocusAreas(profile, round))
                    .build());
            index++;
        }

        String briefing = String.format("This %s placement track is designed to mirror %s's hiring pattern with a balanced focus on aptitude, coding, communication, and technical evaluation.",
                profile.getCompanyName(), profile.getCompanyName());

        return CompanyMockInterviewResponse.builder()
                .companyId(profile.getId())
                .companyName(profile.getCompanyName())
                .difficulty(profile.getDifficulty() != null ? profile.getDifficulty().name() : "MEDIUM")
                .rounds(interviewRounds)
                .briefing(briefing)
                .build();
    }

    private List<String> parseInterviewRounds(String interviewRoundsJson) {
        if (interviewRoundsJson == null || interviewRoundsJson.isBlank()) {
            return List.of("Aptitude", "Technical", "HR");
        }

        try {
            return objectMapper.readValue(interviewRoundsJson, new TypeReference<>() {});
        } catch (Exception ex) {
            return splitJsonString(interviewRoundsJson);
        }
    }

    private List<String> splitJsonString(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of("Aptitude", "Technical", "HR");
        }
        String cleaned = raw.replace("[", "").replace("]", "").replace("\"", "");
        String[] parts = cleaned.split(",");
        List<String> values = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                values.add(trimmed);
            }
        }
        return values.isEmpty() ? List.of("Aptitude", "Technical", "HR") : values;
    }

    private String buildRoundDescription(CompanyProfile profile, String round) {
        if (round.toLowerCase().contains("aptitude")) {
            return "Solve quantitative aptitude and reasoning questions aligned to the company's hiring pattern.";
        }
        if (round.toLowerCase().contains("hr") || round.toLowerCase().contains("communication")) {
            return "Practice behavioral and communication questions with a focus on company values and culture fit.";
        }
        return "Tackle technical questions and coding problems that reflect the company’s technical expectations.";
    }

    private List<String> buildFocusAreas(CompanyProfile profile, String round) {
        if (round.toLowerCase().contains("aptitude")) {
            return List.of("Quantitative aptitude", "Logical reasoning", "Data interpretation");
        }
        if (round.toLowerCase().contains("hr")) {
            return List.of("Behavioral questions", "STAR responses", "Communication clarity");
        }
        return List.of("Topic knowledge", "Coding proficiency", "System design basics");
    }
}
