package com.aelamel.ratelimiter.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RateLimitConfigRepository extends JpaRepository<RateLimitRule, String> {

  RateLimitRule findFirstByClientIdAndActive(String clientId, boolean active);
}
