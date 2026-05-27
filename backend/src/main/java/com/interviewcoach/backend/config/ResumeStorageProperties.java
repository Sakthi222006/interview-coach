package com.interviewcoach.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Data
@Configuration
@ConfigurationProperties(prefix = "resume")
public class ResumeStorageProperties {

    private String storagePath = "uploads/resumes";
    private String maxFileSize = "10MB";

    public long getMaxFileSizeBytes() {
        String normalized = maxFileSize.trim().toUpperCase();
        if (normalized.endsWith("MB")) {
            try {
                return Long.parseLong(normalized.replace("MB", "")) * 1024 * 1024;
            } catch (NumberFormatException ex) {
                return 10L * 1024 * 1024;
            }
        }
        if (normalized.endsWith("KB")) {
            try {
                return Long.parseLong(normalized.replace("KB", "")) * 1024;
            } catch (NumberFormatException ex) {
                return 10L * 1024 * 1024;
            }
        }
        try {
            return Long.parseLong(normalized);
        } catch (NumberFormatException ex) {
            return 10L * 1024 * 1024;
        }
    }

    @PostConstruct
    public void normalizePath() {
        if (storagePath.endsWith("/")) {
            storagePath = storagePath.substring(0, storagePath.length() - 1);
        }
    }
}
