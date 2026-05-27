package com.interviewcoach.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiClient {

    private final GeminiConfig geminiConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String callGemini(String prompt) {
        String url = geminiConfig.getApiUrl() + "?key=" + geminiConfig.getApiKey();
        log.info("Gemini client calling url={} apiKeyPresent={}", geminiConfig.getApiUrl(), geminiConfig.getApiKey() != null && !geminiConfig.getApiKey().isBlank());

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
            ),
            "generationConfig", Map.of(
                "temperature",     0.3,
                "maxOutputTokens", 2048,
                "topP",            0.8
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        long[] backoffMs = {1000L, 2000L, 4000L};
        for (int attempt = 1; attempt <= backoffMs.length; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    new HttpEntity<>(requestBody, headers),
                    String.class
                );

                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    log.info("Gemini returned HTTP 200 for url={}", url);
                    return extractText(response.getBody());
                }

                log.error("Gemini non-200 status: {} responseBody={}", response.getStatusCode(), response.getBody());
                if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS && attempt < backoffMs.length) {
                    log.warn("Gemini rate-limited on attempt {}. Retrying after {}ms.", attempt, backoffMs[attempt - 1]);
                    sleep(backoffMs[attempt - 1]);
                    continue;
                }
                throw new GeminiApiException("Gemini request failed with status " + response.getStatusCode(), false, null);

            } catch (HttpStatusCodeException e) {
                log.error("Gemini HTTP exception on attempt {} status={} body={}", attempt, e.getStatusCode(), e.getResponseBodyAsString());
                if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS && attempt < backoffMs.length) {
                    log.warn("Gemini quota exceeded on attempt {}. Retrying after {}ms.", attempt, backoffMs[attempt - 1]);
                    sleep(backoffMs[attempt - 1]);
                    continue;
                }
                boolean quotaExceeded = e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS;
                throw new GeminiApiException("Gemini quota exceeded", quotaExceeded, e);
            } catch (Exception e) {
                log.error("Gemini call failed on attempt {}", attempt, e);
                if (attempt < backoffMs.length) {
                    log.warn("Retrying Gemini after {}ms due to transient error.", backoffMs[attempt - 1]);
                    sleep(backoffMs[attempt - 1]);
                    continue;
                }
                throw new GeminiApiException("Gemini call failed", false, e);
            }
        }

        throw new GeminiApiException("Gemini call failed after retries", false, null);
    }

    private String extractText(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            return root
                .path("candidates").get(0)
                .path("content")
                .path("parts").get(0)
                .path("text")
                .asText();
        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
            return null;
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
