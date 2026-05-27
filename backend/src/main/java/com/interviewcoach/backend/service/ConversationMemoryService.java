package com.interviewcoach.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewcoach.backend.model.VoiceInterviewSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationMemoryService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> getConversationHistory(VoiceInterviewSession session) {
        try {
            if (session.getConversationMemoryJson() == null || session.getConversationMemoryJson().isBlank()) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(session.getConversationMemoryJson(), new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("Failed to parse conversation memory json", e);
            return new ArrayList<>();
        }
    }

    public void addMemory(VoiceInterviewSession session, String transcript) {
        List<String> history = getConversationHistory(session);
        history.add(transcript);
        try {
            session.setConversationMemoryJson(objectMapper.writeValueAsString(history));
        } catch (Exception e) {
            log.error("Failed to serialize conversation memory", e);
        }
    }
}
