package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.ApiResponse;
import com.interviewcoach.backend.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getQuestions(
        @RequestParam String topic,
        @RequestParam String difficulty,
        @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
            questionService.getQuestions(topic, difficulty, limit),
            "Questions fetched"
        ));
    }

    @GetMapping("/topics")
    public ResponseEntity<ApiResponse<List<String>>> getTopics() {
        return ResponseEntity.ok(ApiResponse.ok(questionService.getTopics(), "Topics fetched"));
    }
}