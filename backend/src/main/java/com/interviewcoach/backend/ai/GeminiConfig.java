package com.interviewcoach.backend.ai;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @PostConstruct
    public void init() {
        String modelName = extractModelName(apiUrl);
        log.info("GeminiConfig loaded endpoint={} modelName={} apiKeyPresent={}",
            apiUrl, modelName, apiKey != null && !apiKey.isBlank() && !apiKey.contains("REPLACE_WITH"));

        if (apiKey == null || apiKey.isBlank() || apiKey.contains("REPLACE_WITH")) {
            log.warn("Gemini API key is not set or is using the placeholder value. Set GEMINI_API_KEY environment variable or update application.properties.");
        }
    }

    private String extractModelName(String url) {
        if (url == null) return "unknown";
        int start = url.indexOf("/models/");
        int end = url.indexOf(":", start >= 0 ? start : 0);
        if (start >= 0 && end > start) {
            return url.substring(start + 8, end);
        }
        return "unknown";
    }

    public String getApiKey() { return apiKey; }
    public String getApiUrl() { return apiUrl; }
}
