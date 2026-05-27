package com.interviewcoach.backend.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class VoiceSessionResponse {

    private Long sessionId;
    private String topic;
    private String targetRole;
    private String difficulty;
    private String status;
    private Integer durationSeconds;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
}
