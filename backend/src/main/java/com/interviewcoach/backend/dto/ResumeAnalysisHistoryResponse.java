package com.interviewcoach.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeAnalysisHistoryResponse {
    private List<ResumeAnalysisHistoryEntry> history;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumeAnalysisHistoryEntry {
        private LocalDateTime analyzedAt;
        private ResumeExtractionResult analysis;
    }
}
