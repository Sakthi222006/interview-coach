package com.interviewcoach.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.dto.*;
import com.interviewcoach.backend.model.*;
import com.interviewcoach.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyProfileRepository companyProfileRepository;
    private final AptitudeQuestionRepository aptitudeQuestionRepository;
    private final CodingChallengeRepository codingChallengeRepository;
    private final CodingSubmissionRepository codingSubmissionRepository;
    private final CodingResultRepository codingResultRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final CompanyInterviewService companyInterviewService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CompanyProfileResponse> getAllProfiles() {
        return companyProfileRepository.findAllByOrderByCompanyNameAsc().stream()
            .map(this::buildProfileResponse)
            .collect(Collectors.toList());
    }

    public List<AptitudeQuestionResponse> getQuestions(Long companyId, String category, String difficulty, int limit) {
        List<AptitudeQuestion> questions;
        if (category != null && difficulty != null) {
            questions = aptitudeQuestionRepository.findByCompanyIdAndCategoryAndDifficulty(
                    companyId,
                    parseCategory(category),
                    parseDifficulty(difficulty)
            );
        } else if (category != null) {
            questions = aptitudeQuestionRepository.findByCompanyIdAndCategory(companyId, parseCategory(category));
        } else if (difficulty != null) {
            questions = aptitudeQuestionRepository.findByCompanyIdAndDifficulty(companyId, parseDifficulty(difficulty));
        } else {
            questions = aptitudeQuestionRepository.findByCompanyId(companyId);
        }

        return questions.stream()
                .limit(Math.max(0, limit))
                .map(this::buildQuestionResponse)
                .collect(Collectors.toList());
    }

    public List<CodingChallengeResponse> getCodingChallenges(Long companyId, String difficulty, String topic) {
        List<CodingChallenge> challenges;
        if (difficulty != null && topic != null) {
            challenges = codingChallengeRepository.findByCompanyIdAndDifficulty(companyId, parseDifficulty(difficulty));
            challenges = filterByTopic(challenges, topic);
        } else if (topic != null) {
            challenges = codingChallengeRepository.findByCompanyIdAndTopicIgnoreCase(companyId, topic);
        } else if (difficulty != null) {
            challenges = codingChallengeRepository.findByCompanyIdAndDifficulty(companyId, parseDifficulty(difficulty));
        } else {
            challenges = codingChallengeRepository.findByCompanyId(companyId);
        }
        return challenges.stream().map(this::buildCodingResponse).collect(Collectors.toList());
    }

    public CodingResultResponse submitCodingSolution(Long challengeId, SubmitCodingSolutionRequest request, User user) {
        CodingChallenge challenge = codingChallengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Coding challenge not found"));

        CodingSubmission submission = CodingSubmission.builder()
                .challenge(challenge)
                .user(user)
                .code(request.getCode())
                .status(CodingSubmission.Status.PENDING)
                .build();

        submission = codingSubmissionRepository.save(submission);

        List<Map<String, String>> testCases = parseTestCases(challenge.getTestCases());
        int totalTests = Math.max(1, testCases.size());
        int passed = evaluateSubmission(request.getCode(), testCases);
        boolean passedAll = passed == totalTests;

        submission.setPassedTests(passed);
        submission.setTotalTests(totalTests);
        submission.setStatus(passedAll ? CodingSubmission.Status.PASSED : CodingSubmission.Status.FAILED);
        submission.setFeedback(buildFeedback(challenge, passed, totalTests));
        codingSubmissionRepository.save(submission);

        CodingResult result = CodingResult.builder()
                .submission(submission)
                .passed(passedAll)
                .passedTests(passed)
                .totalTests(totalTests)
                .details(submission.getFeedback())
                .build();
        codingResultRepository.save(result);

        return CodingResultResponse.builder()
                .challengeId(challenge.getId())
                .title(challenge.getTitle())
                .passed(passedAll)
                .passedTests(passed)
                .totalTests(totalTests)
                .feedback(submission.getFeedback())
                .build();
    }

    public CompanyMockInterviewResponse getMockInterview(Long companyId) {
        return companyInterviewService.generateMockInterview(companyId);
    }

    public CompanyReadinessResponse getReadiness(Long companyId, Long userId) {
        CompanyProfile profile = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        int resumeScore = findLatestResumeScore(userId).orElse(50);
        int aptitudeScore = Math.min(100, Math.max(0, Math.round((profile.getAptitudeWeightage() != null ? profile.getAptitudeWeightage().floatValue() : 20f))));
        int codingScore = Math.min(100, Math.max(0, Math.round((profile.getCodingWeightage() != null ? profile.getCodingWeightage().floatValue() : 20f))));
        int communicationScore = Math.min(100, Math.max(0, Math.round((profile.getCommunicationWeightage() != null ? profile.getCommunicationWeightage().floatValue() : 20f))));
        int interviewScore = Math.min(100, Math.max(0, Math.round(((profile.getTechnicalWeightage() != null ? profile.getTechnicalWeightage().floatValue() : 20f)
                + (profile.getCodingWeightage() != null ? profile.getCodingWeightage().floatValue() : 20f)) / 2f)));

        int baseScore = 45 + Math.round((profile.getAptitudeWeightage() != null ? profile.getAptitudeWeightage().floatValue() : 20f) / 4f)
                + Math.round((profile.getCodingWeightage() != null ? profile.getCodingWeightage().floatValue() : 20f) / 4f)
                + Math.round(resumeScore / 10f);

        int finalScore = Math.min(100, Math.max(0, baseScore));
        String level = finalScore >= 80 ? "Strong" : finalScore >= 60 ? "Moderate" : "Needs improvement";

        List<String> strengths = new ArrayList<>();
        List<String> improvementAreas = new ArrayList<>();

        if (profile.getCodingWeightage() != null && profile.getCodingWeightage() >= 30) {
            strengths.add("Coding practice aligned to company expectations");
        } else {
            improvementAreas.add("Increase coding problem practice");
        }
        if (profile.getAptitudeWeightage() != null && profile.getAptitudeWeightage() >= 25) {
            strengths.add("Aptitude readiness for the company");
        }
        if (resumeScore < 70) {
            improvementAreas.add("Improve resume clarity and job fit");
        }
        if (profile.getCommunicationWeightage() != null && profile.getCommunicationWeightage() >= 20) {
            improvementAreas.add("Practice behavioral answers and communication skills");
        }

        return CompanyReadinessResponse.builder()
                .companyId(profile.getId())
                .companyName(profile.getCompanyName())
                .readinessScore(finalScore)
                .readinessLevel(level)
                .resumeScore(resumeScore)
                .aptitudeScore(aptitudeScore)
                .codingScore(codingScore)
                .communicationScore(communicationScore)
                .interviewScore(interviewScore)
                .summary(String.format("Based on your resume score and %s's placement weights, your readiness is %s.", profile.getCompanyName(), level))
                .strengths(strengths.isEmpty() ? List.of("Foundational company prep topics identified") : strengths)
                .improvementAreas(improvementAreas.isEmpty() ? List.of("Keep practicing interview questions and update your resume") : improvementAreas)
                .build();
    }

    public CompanyReadinessResponse getReadinessByCompanyName(String companyName, Long userId) {
        return getReadiness(getCompanyIdByName(companyName), userId);
    }

    public CompanyProfileResponse getProfileByCompanyName(String companyName) {
        return buildProfileResponse(getCompanyByName(companyName));
    }

    public List<AptitudeQuestionResponse> getQuestionsByCompanyName(String companyName, String category, String difficulty, int limit) {
        return getQuestions(getCompanyIdByName(companyName), category, difficulty, limit);
    }

    public List<CodingChallengeResponse> getCodingChallengesByCompanyName(String companyName, String difficulty, String topic) {
        return getCodingChallenges(getCompanyIdByName(companyName), difficulty, topic);
    }

    private Long getCompanyIdByName(String companyName) {
        return getCompanyByName(companyName).getId();
    }

    private CompanyProfile getCompanyByName(String companyName) {
        return companyProfileRepository.findByCompanyNameIgnoreCase(companyName)
                .orElseThrow(() -> new IllegalArgumentException("Company not found: " + companyName));
    }

    private CompanyProfileResponse buildProfileResponse(CompanyProfile profile) {
        return CompanyProfileResponse.builder()
                .id(profile.getId())
                .companyName(profile.getCompanyName())
                .description(profile.getDescription())
                .difficulty(profile.getDifficulty())
                .hiringPattern(profile.getHiringPattern())
                .interviewRounds(parseInterviewRounds(profile.getInterviewRounds()))
                .aptitudeWeightage(profile.getAptitudeWeightage())
                .codingWeightage(profile.getCodingWeightage())
                .communicationWeightage(profile.getCommunicationWeightage())
                .technicalWeightage(profile.getTechnicalWeightage())
                .focusTechnologies(parseJsonList(profile.getFocusTechnologies()))
                .build();
    }

    private AptitudeQuestionResponse buildQuestionResponse(AptitudeQuestion question) {
        return AptitudeQuestionResponse.builder()
                .id(question.getId())
                .question(question.getQuestion())
                .category(question.getCategory())
                .difficulty(question.getDifficulty())
                .options(List.of(question.getOptionA(), question.getOptionB(), question.getOptionC(), question.getOptionD()))
                .correctAnswer(question.getCorrectAnswer())
                .explanation(question.getExplanation())
                .timeLimit(question.getTimeLimit())
                .topic(question.getTopic())
                .build();
    }

    private CodingChallengeResponse buildCodingResponse(CodingChallenge challenge) {
        return CodingChallengeResponse.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .difficulty(challenge.getDifficulty())
                .topic(challenge.getTopic())
                .exampleInput(challenge.getExampleInput())
                .exampleOutput(challenge.getExampleOutput())
                .constraints(challenge.getConstraints())
                .timeLimit(challenge.getTimeLimit())
                .memoryLimit(challenge.getMemoryLimit())
                .acceptanceRate(challenge.getAcceptanceRate())
                .relatedTopics(challenge.getRelatedTopics())
                .build();
    }

    private Optional<Integer> findLatestResumeScore(Long userId) {
        return resumeAnalysisRepository.findTopByUserIdOrderByAnalyzedAtDesc(userId)
                .map(ResumeAnalysis::getResumeScore);
    }

    private List<String> parseInterviewRounds(String interviewRoundsJson) {
        if (interviewRoundsJson == null || interviewRoundsJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(interviewRoundsJson, new TypeReference<>() {});
        } catch (Exception ex) {
            return splitJsonString(interviewRoundsJson);
        }
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception ex) {
            return splitJsonString(json);
        }
    }

    private List<String> splitJsonString(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        String cleaned = raw.replace("[", "").replace("]", "").replace("\"", "");
        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private AptitudeQuestion.Category parseCategory(String category) {
        try {
            return AptitudeQuestion.Category.valueOf(category.trim().toUpperCase());
        } catch (Exception ex) {
            return AptitudeQuestion.Category.QUANTITATIVE;
        }
    }

    private CompanyProfile.DifficultyLevel parseDifficulty(String difficulty) {
        try {
            return CompanyProfile.DifficultyLevel.valueOf(difficulty.trim().toUpperCase());
        } catch (Exception ex) {
            return CompanyProfile.DifficultyLevel.MEDIUM;
        }
    }

    private List<CodingChallenge> filterByTopic(List<CodingChallenge> challenges, String topic) {
        if (topic == null || topic.isBlank()) {
            return challenges;
        }
        return challenges.stream()
                .filter(challenge -> challenge.getTopic() != null && challenge.getTopic().equalsIgnoreCase(topic))
                .collect(Collectors.toList());
    }

    private List<Map<String, String>> parseTestCases(String testCases) {
        if (testCases == null || testCases.isBlank()) {
            return List.of(Map.of("input", "", "output", ""));
        }
        try {
            return objectMapper.readValue(testCases, new TypeReference<>() {});
        } catch (Exception ex) {
            return List.of(Map.of("input", "", "output", testCases));
        }
    }

    private int evaluateSubmission(String code, List<Map<String, String>> testCases) {
        if (code == null || code.isBlank()) {
            return 0;
        }
        int passed = 0;
        String source = code.toLowerCase();
        for (Map<String, String> testCase : testCases) {
            String expected = Optional.ofNullable(testCase.get("output")).orElse("").toLowerCase();
            if (!expected.isBlank() && source.contains(expected.substring(0, Math.min(8, expected.length())))) {
                passed++;
            }
        }
        if (passed == 0 && source.length() > 80) {
            passed = Math.min(testCases.size(), 1);
        }
        return passed;
    }

    private String buildFeedback(CodingChallenge challenge, int passed, int total) {
        if (passed == total) {
            return "Great job! Your submission matched the primary expected response pattern and passed all evaluated cases.";
        }
        if (passed > 0) {
            return String.format("Your submission passed %d of %d checks. Review the constraints and edge cases for a stronger result.", passed, total);
        }
        return "Your code did not match the expected result pattern. Check the problem statement, input/output examples, and try again.";
    }
}
