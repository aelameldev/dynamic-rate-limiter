package com.aelamel.ratelimiter.constants;

public class RateLimitConstants {

  private RateLimitConstants() {
    // Prevent instantiation
  }

  public static final String RATE_LIMIT_HEADER = "X-RateLimit-Limit";
  public static final String RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining";
  public static final String RATE_LIMIT_RETRY_AFTER_HEADER = "Retry-After";


  public static final String RATE_LIMIT_EXCEEDED_MESSAGE = "Rate limit exceeded. Please try again later.";

  public static final String STRATEGY_SUFFIX = "RateLimiter";
}
