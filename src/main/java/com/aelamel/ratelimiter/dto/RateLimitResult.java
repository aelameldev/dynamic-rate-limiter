package com.aelamel.ratelimiter.dto;

public record RateLimitResult(boolean isAllowed, Long retryAfterSeconds, long remainingRequests) {

    public static RateLimitResult allowed(long remainingRequests) {
        return new RateLimitResult(true, null, remainingRequests);
    }

    public static RateLimitResult notAllowed(Long retryAfterSeconds) {
        return new RateLimitResult(false, retryAfterSeconds, 0);
    }

}
