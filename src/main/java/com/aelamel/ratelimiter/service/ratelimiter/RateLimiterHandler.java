package com.aelamel.ratelimiter.service.ratelimiter;

import com.aelamel.ratelimiter.dto.RateLimitResult;

public interface RateLimiterHandler {

  RateLimitResult validate(String clientId);

}

