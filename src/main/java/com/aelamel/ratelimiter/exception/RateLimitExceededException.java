package com.aelamel.ratelimiter.exception;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {
  private final long retryAfterSeconds;
  private final long limit;

  public RateLimitExceededException(String message, long retryAfterSeconds, long limit) {
    super(message);
    this.retryAfterSeconds = retryAfterSeconds;
    this.limit = limit;
  }
}
