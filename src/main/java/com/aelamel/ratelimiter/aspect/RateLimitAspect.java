package com.aelamel.ratelimiter.aspect;


import static com.aelamel.ratelimiter.constants.RateLimitConstants.RATE_LIMIT_EXCEEDED_MESSAGE;
import static com.aelamel.ratelimiter.constants.RateLimitConstants.RATE_LIMIT_REMAINING_HEADER;
import com.aelamel.ratelimiter.domain.RateLimitRule;
import com.aelamel.ratelimiter.dto.RateLimitResult;
import com.aelamel.ratelimiter.exception.RateLimitExceededException;
import com.aelamel.ratelimiter.monitoring.RateLimitMetrics;
import com.aelamel.ratelimiter.resolvers.RateLimiterResolver;
import com.aelamel.ratelimiter.service.RateLimitConfigService;
import com.aelamel.ratelimiter.service.ratelimiter.RateLimiterHandler;
import com.aelamel.ratelimiter.utils.UserContext;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitAspect {

  private final RateLimiterResolver rateLimiterResolver;
  private final RateLimitConfigService rateLimitConfigService;
  private final RateLimitMetrics rateLimitMetrics;

  @Around("@annotation(com.aelamel.ratelimiter.annotation.RateLimited)")
  public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
    String clientId = UserContext.getClientId();

    if (clientId == null) {
      log.warn("No client ID found in security context, skipping rate limiting");
      return joinPoint.proceed();
    }
    RateLimitRule rateLimitConfiguration = rateLimitConfigService.getConfigForClient(clientId);
    RateLimiterHandler rateLimiter = rateLimiterResolver.resolve(clientId);

    RateLimitResult rateLimitResult = rateLimiter.validate(clientId);

    if (!rateLimitResult.isAllowed()) {
      rateLimitMetrics.recordDeniedRequest();
      throw new RateLimitExceededException(RATE_LIMIT_EXCEEDED_MESSAGE, rateLimitResult.retryAfterSeconds(), rateLimitConfiguration.getRequestLimit());
    }

    rateLimitMetrics.recordAllowedRequest();

    Optional.of(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast)
        .map(ServletRequestAttributes::getResponse)
        .ifPresent(response -> response.setHeader(RATE_LIMIT_REMAINING_HEADER, String.valueOf(rateLimitResult.remainingRequests())));

    return joinPoint.proceed();
  }
}