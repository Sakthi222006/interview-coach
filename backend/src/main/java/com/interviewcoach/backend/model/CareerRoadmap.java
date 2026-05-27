package com.interviewcoach.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "career_roadmap")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerRoadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String title;

    @Lob
    private String roadmapJson;
}
