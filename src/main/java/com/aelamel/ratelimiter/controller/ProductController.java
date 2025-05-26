package com.aelamel.ratelimiter.controller;


import com.aelamel.ratelimiter.annotation.RateLimited;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductController {

  @GetMapping
  @RateLimited
  public List<String> getProducts() {
    return List.of("Product 1", "Product 2", "Product 3");
  }

}
