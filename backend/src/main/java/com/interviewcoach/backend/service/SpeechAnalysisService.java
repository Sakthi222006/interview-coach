package com.interviewcoach.backend.service;

import com.interviewcoach.backend.dto.SpeechAnalysisResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SpeechAnalysisService {

    private static final List<String> FILLER_WORDS = List.of(
        "um", "uh", "like", "you know", "actually", "basically", "so", "right", "well", "I mean"
    );

    public SpeechAnalysisResult analyze(String transcript, Integer durationSeconds) {
        String normalized = transcript == null ? "" : transcript.trim();
        int wordCount = countWords(normalized);
        int fillerCount = countFillers(normalized);
        double wordsPerMinute = calculateWpm(wordCount, durationSeconds);
        int sentenceCount = countSentences(normalized);
        double avgSentenceLength = sentenceCount > 0 ? (double) wordCount / sentenceCount : wordCount;

        int fillerPenalty = (int) Math.min(40, fillerCount * 8);
        int lengthPenalty = (int) Math.max(0, Math.round((avgSentenceLength - 18) * 1.5));
        int grammarScore = clamp(100 - fillerPenalty - lengthPenalty);
        int clarityScore = clamp(95 - (int) Math.max(0, Math.round((avgSentenceLength - 18) * 1.2)) - fillerPenalty);
        int confidenceScore = clamp((int) Math.round(90 - fillerCount * 6 - Math.max(0, 130 - wordsPerMinute) * 0.12));
        int completenessScore = clamp(Math.min(100, wordCount * 2));

        return SpeechAnalysisResult.builder()
            .wordCount(wordCount)
            .fillerWordCount(fillerCount)
            .speakingRateWpm(Math.round(wordsPerMinute * 10.0) / 10.0)
            .grammarScore(grammarScore)
            .clarityScore(clarityScore)
            .confidenceScore(confidenceScore)
            .completenessScore(completenessScore)
            .build();
    }

    private int countWords(String transcript) {
        if (transcript.isBlank()) {
            return 0;
        }
        Matcher matcher = Pattern.compile("\\b[\\p{L}'-]+\\b").matcher(transcript);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private int countFillers(String transcript) {
        if (transcript.isBlank()) {
            return 0;
        }
        String lower = transcript.toLowerCase();
        int total = 0;
        for (String filler : FILLER_WORDS) {
            Matcher matcher = Pattern.compile("\\b" + Pattern.quote(filler) + "\\b", Pattern.CASE_INSENSITIVE).matcher(lower);
            while (matcher.find()) {
                total++;
            }
        }
        return total;
    }

    private int countSentences(String transcript) {
        if (transcript.isBlank()) {
            return 0;
        }
        String[] sentences = transcript.split("[.!?]+\\s*");
        return (int) java.util.Arrays.stream(sentences)
            .filter(s -> !s.isBlank())
            .count();
    }

    private double calculateWpm(int wordCount, Integer durationSeconds) {
        if (durationSeconds == null || durationSeconds <= 0) {
            return 0.0;
        }
        double minutes = Math.max(0.5, durationSeconds / 60.0);
        return wordCount / minutes;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }
}
