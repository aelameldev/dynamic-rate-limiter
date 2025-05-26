package com.aelamel.ratelimiter.exception;

import static com.aelamel.ratelimiter.constants.RateLimitConstants.RATE_LIMIT_HEADER;
import static com.aelamel.ratelimiter.constants.RateLimitConstants.RATE_LIMIT_RETRY_AFTER_HEADER;
import com.aelamel.ratelimiter.dto.ErrorResponse;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(RateLimitExceededException.class)
  public ResponseEntity<ErrorResponse> handleRateLimitExceededException(RateLimitExceededException ex) {
    log.error("Rate limit exceeded: {}", ex.getMessage());

    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.TOO_MANY_REQUESTS.value(),
        "Too Many Requests",
        ex.getMessage(),
        Instant.now().toString()
    );

    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .header(RATE_LIMIT_RETRY_AFTER_HEADER, String.valueOf(ex.getRetryAfterSeconds()))
        .header(RATE_LIMIT_HEADER, String.valueOf(ex.getLimit()))
        .body(errorResponse);
  }

}
