package com.interviewcoach.backend.ai;

public class GeminiApiException extends RuntimeException {
    private final boolean quotaExceeded;

    public GeminiApiException(String message, boolean quotaExceeded, Throwable cause) {
        super(message, cause);
        this.quotaExceeded = quotaExceeded;
    }

    public boolean isQuotaExceeded() {
        return quotaExceeded;
    }
}
