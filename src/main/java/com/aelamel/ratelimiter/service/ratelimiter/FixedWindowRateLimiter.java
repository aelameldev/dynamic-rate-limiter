package com.aelamel.ratelimiter.service.ratelimiter;


import static com.aelamel.ratelimiter.constants.RateLimitConstants.STRATEGY_SUFFIX;
import com.aelamel.ratelimiter.domain.RateLimitRule;
import com.aelamel.ratelimiter.dto.RateLimitResult;
import com.aelamel.ratelimiter.service.RateLimitConfigService;
import com.aelamel.ratelimiter.types.RateLimitStrategy.RateLimitStategies;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component(value = RateLimitStategies.FIXED_WINDOW + STRATEGY_SUFFIX)
@Slf4j
@RequiredArgsConstructor
public class FixedWindowRateLimiter implements RateLimiterHandler {

  private final RedisTemplate<String, String> redisTemplate;
  private final RateLimitConfigService rateLimitConfigService;

  @Override
  public RateLimitResult validate(String clientId) {
    RateLimitRule config = rateLimitConfigService.getConfigForClient(clientId);
    String key = generateKey(clientId, config);

    long ttlSeconds = config.getDurationUnit().getSeconds();

    long currentCount = redisTemplate.opsForValue().increment(key, 1);

    if (currentCount == 1) {
      redisTemplate.expire(key, ttlSeconds, TimeUnit.SECONDS);
    }

    if (currentCount > config.getRequestLimit()) {
      log.error("Rate limit exceeded for client: {}", clientId);
      return RateLimitResult.notAllowed(
          redisTemplate.getExpire(key, TimeUnit.SECONDS)
      );
    }

    return RateLimitResult.allowed(
        config.getRequestLimit() - currentCount
    );
  }

  private String generateKey(String clientId, RateLimitRule config) {
    long windowStart = Instant.now().getEpochSecond() / config.getDurationUnit().getSeconds();
    return String.format("ratelimit:%s:%s:%d", config.getStrategy(), clientId, windowStart);
  }
}
