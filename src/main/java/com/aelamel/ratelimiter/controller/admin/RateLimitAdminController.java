package com.aelamel.ratelimiter.controller.admin;


import com.aelamel.ratelimiter.domain.RateLimitRule;
import com.aelamel.ratelimiter.service.RateLimitConfigService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/rate-limits")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class RateLimitAdminController {


  final RateLimitConfigService rateLimitConfigService;


  @PostMapping("")
  public RateLimitRule create(@RequestBody RateLimitRule rateLimitConfiguration) {
    return rateLimitConfigService.saveConfig(rateLimitConfiguration);
  }
}
