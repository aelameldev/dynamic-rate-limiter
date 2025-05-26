package com.aelamel.ratelimiter.types;

import lombok.Getter;

@Getter
public enum RateLimitStrategy {
  FIXED_WINDOW(RateLimitStategies.FIXED_WINDOW),
  SLIDING_WINDOW(RateLimitStategies.SLIDING_WINDOW),
  TOKEN_BUCKET(RateLimitStategies.TOKEN_BUKET),
  LEAKY_BUCKET(RateLimitStategies.LEAKY_BUKET)
  ;

  private final String strategy;

  RateLimitStrategy(String strategy) {
    this.strategy = strategy;
  }

  public static class RateLimitStategies {

    public static final String FIXED_WINDOW = "FIXED_WINDOW";
    public static final String SLIDING_WINDOW = "SLIDING_WINDOW";

    public static final String TOKEN_BUKET = "TOKEN_BUCKET";
    public static final String LEAKY_BUKET = "LEAKY_BUCKET";
  }
}
