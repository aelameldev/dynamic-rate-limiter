package com.aelamel.ratelimiter.service.ratelimiter;


import com.aelamel.ratelimiter.dto.RateLimitResult;
import org.springframework.stereotype.Component;

@Component
public class NoOpRateLimiter implements RateLimiterHandler {

  @Override
  public RateLimitResult validate(String clientId) {
    return RateLimitResult.allowed(Long.MAX_VALUE);
  }
}
