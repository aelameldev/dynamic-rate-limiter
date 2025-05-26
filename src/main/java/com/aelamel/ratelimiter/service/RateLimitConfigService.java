package com.aelamel.ratelimiter.service;


import com.aelamel.ratelimiter.domain.RateLimitConfigRepository;
import com.aelamel.ratelimiter.domain.RateLimitRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RateLimitConfigService {


  private final RateLimitConfigRepository configRepository;

  @Transactional(readOnly = true)
  @Cacheable(value = "rateLimitConfig", key = "#clientId", unless = "#result == null")
  public RateLimitRule getConfigForClient(String clientId) {
    return configRepository.findFirstByClientIdAndActive(clientId, true);
  }

  @CacheEvict(value = "rateLimitConfig", key = "#config.clientId", allEntries = true)
  public RateLimitRule saveConfig(RateLimitRule config) {
    log.info("Saving rate limit configuration for client: {}", config.getClientId());
    return configRepository.save(config);
  }


}
