package com.aelamel.ratelimiter.service.ratelimiter;


import static com.aelamel.ratelimiter.constants.RateLimitConstants.STRATEGY_SUFFIX;
import com.aelamel.ratelimiter.domain.RateLimitRule;
import com.aelamel.ratelimiter.dto.RateLimitResult;
import com.aelamel.ratelimiter.exception.RateLimitExceededException;
import com.aelamel.ratelimiter.service.RateLimitConfigService;
import com.aelamel.ratelimiter.types.RateLimitStrategy.RateLimitStategies;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Component(value = RateLimitStategies.SLIDING_WINDOW + STRATEGY_SUFFIX)
public class SlidingWindowRateLimiter implements RateLimiterHandler {

  private final DefaultRedisScript<Long> slidingWindowScript;
  private final RedisTemplate<String, String> redisTemplate;
  private final RateLimitConfigService rateLimitConfigService;


  public SlidingWindowRateLimiter(RedisTemplate<String, String> redisTemplate,
      RateLimitConfigService rateLimitConfigService) {
    this.redisTemplate = redisTemplate;
    this.rateLimitConfigService = rateLimitConfigService;
    DefaultRedisScript<Long> slidingWindowScript = new DefaultRedisScript<>();

    slidingWindowScript.setScriptText("""
        local key = KEYS[1]
        local now = tonumber(ARGV[1])
        local window = tonumber(ARGV[2])
        local limit = tonumber(ARGV[3])

        -- Your rate limiting logic here
        redis.call('ZREMRANGEBYSCORE', key, 0, now - window)
        local count = redis.call('ZCARD', key)
            
        if count < limit then
            redis.call('ZADD', key, now, now .. '-' .. math.random())
            redis.call('EXPIRE', key, window)
            return count + 1
        else
            return -1
        end
        """);
    slidingWindowScript.setResultType(Long.class);
    this.slidingWindowScript = slidingWindowScript;
  }

  @Override
  public RateLimitResult validate(String clientId) throws RateLimitExceededException {

    RateLimitRule rateLimitConfig = rateLimitConfigService.getConfigForClient(clientId);

    String redisKey = generateKey(clientId, rateLimitConfig);
    long now = Instant.now().getEpochSecond();
    int limit = rateLimitConfig.getRequestLimit();
    int window = rateLimitConfig.getDurationUnit().getSeconds();

    long result = redisTemplate.execute(
        slidingWindowScript,
        List.of(redisKey),
        String.valueOf(now),
        String.valueOf(window),
        String.valueOf(limit)
    );

    return result > 0 ? RateLimitResult.allowed(rateLimitConfig.getRequestLimit() - result)
        : RateLimitResult.notAllowed(redisTemplate.getExpire(redisKey, TimeUnit.SECONDS));
  }

  private String generateKey(String clientId, RateLimitRule config) {
    return String.format("ratelimit:%s:%s", config.getStrategy(), clientId);
  }
}
