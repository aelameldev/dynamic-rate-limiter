package com.aelamel.ratelimiter.resolvers;

import static com.aelamel.ratelimiter.constants.RateLimitConstants.STRATEGY_SUFFIX;
import com.aelamel.ratelimiter.domain.RateLimitRule;
import com.aelamel.ratelimiter.service.ratelimiter.NoOpRateLimiter;
import com.aelamel.ratelimiter.service.RateLimitConfigService;
import com.aelamel.ratelimiter.service.ratelimiter.RateLimiterHandler;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateLimiterResolver {

  final RateLimitConfigService rateLimitConfigService;
  final Map<String, RateLimiterHandler> rateLimiters;
  final NoOpRateLimiter noOpRateLimiter;

  public RateLimiterHandler resolve(String clientId) {

    RateLimitRule clientConfiguration = rateLimitConfigService.getConfigForClient(clientId);

    if (clientConfiguration == null) {
      return noOpRateLimiter;
    }

    return rateLimiters.getOrDefault(clientConfiguration.getStrategy().getStrategy() + STRATEGY_SUFFIX, noOpRateLimiter);

  }
}
