package com.aelamel.ratelimiter.domain;

import com.aelamel.ratelimiter.types.DurationUnit;
import com.aelamel.ratelimiter.types.RateLimitStrategy;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rate_limit_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitRule {

  @Id
  @NotBlank(message = "Client ID is required")
  private String clientId;

  @Min(value = 1, message = "Max requests must be at least 1")
  private int requestLimit;

  @Enumerated(EnumType.STRING)
  private DurationUnit durationUnit;

  @Enumerated(EnumType.STRING)
  private RateLimitStrategy strategy;

  private boolean active;

  private double refillRate; // For token bucket
  private int bucketCapacity; // For token/leaky bucket


}
