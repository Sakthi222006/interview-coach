package com.interviewcoach.backend.repository;

import com.interviewcoach.backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.repository.query.Param;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByTopicAndDifficulty(String topic, String difficulty);

    List<Question> findByTopic(String topic);

    // Fetch N random questions for a topic+difficulty — used when starting sessions
    // RAND() in MySQL returns rows in random order; LIMIT restricts count
   @Query(
    value = """
        SELECT * FROM questions
        WHERE topic = :topic
        AND difficulty = :difficulty
        ORDER BY RAND()
        LIMIT :limit
        """,
    nativeQuery = true
)
List<Question> findRandomByTopicAndDifficulty(
        @Param("topic") String topic,
        @Param("difficulty") String difficulty,
        @Param("limit") int limit
);

    long countByTopicAndDifficulty(String topic, String difficulty);
}