package com.aelamel.ratelimiter.types;

import lombok.Getter;

@Getter
public enum DurationUnit {
  SECONDS(1),
  MINUTES(60),
  HOURS(3600),
  DAYS(86400),
  MONTHS(2592000);

  private final int seconds;

  DurationUnit(int seconds) {
    this.seconds = seconds;
  }
}
