package com.interviewcoach.backend.controller;

import com.interviewcoach.backend.dto.RoadmapRequest;
import com.interviewcoach.backend.dto.RoadmapResponse;
import com.interviewcoach.backend.service.CareerRoadmapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final CareerRoadmapService roadmapService;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestBody RoadmapRequest request) {
        var opt = roadmapService.generateRoadmap(request);
        if (opt.isPresent()) return ResponseEntity.ok(opt.get());
        return ResponseEntity.badRequest().body("Could not generate roadmap");
    }

    @GetMapping
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("Roadmap API is available");
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getForUser(@PathVariable Long userId) {
        return ResponseEntity.ok("Roadmap retrieval not implemented yet for user=" + userId);
    }
}
