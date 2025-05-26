package com.aelamel.ratelimiter.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class RateLimitMetrics {

  private final Counter allowedRequests;
  private final Counter deniedRequests;

  public RateLimitMetrics(MeterRegistry meterRegistry) {

    this.allowedRequests = Counter.builder("rate_limit.requests.allowed")
        .description("Number of requests allowed by rate limiter")
        .tag("component", "rate-limiter")
        .register(meterRegistry);

    this.deniedRequests = Counter.builder("rate_limit.requests.denied")
        .description("Number of requests denied by rate limiter")
        .tag("component", "rate-limiter")
        .register(meterRegistry);

  }

  public void recordAllowedRequest() {
    allowedRequests.increment();
  }

  public void recordDeniedRequest() {
    deniedRequests.increment();
  }

}