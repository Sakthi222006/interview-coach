package com.interviewcoach.backend.service;

import com.interviewcoach.backend.dto.QuestionResponse;
import com.interviewcoach.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<QuestionResponse> getQuestions(String topic, String difficulty, int limit) {
        return questionRepository
            .findRandomByTopicAndDifficulty(topic.toUpperCase(), difficulty.toUpperCase(), limit)
            .stream()
            .map(QuestionResponse::from)
            .toList();
    }

    public List<String> getTopics() {
        return List.of("DSA", "JAVA", "SQL", "REACT", "HR");
    }
}