package com.example.apigateway;

import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.PrincipalNameKeyResolver;
import org.springframework.http.HttpHeaders;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("service1_route", r -> r
                        .path("/service1/**")
                        .filters(f -> f
                                .requestRateLimiter(config -> config.setKeyResolver(redisKeyResolver()))
                                .addResponseHeader(HttpHeaders.CACHE_CONTROL, "max-age=3600"))
                        .uri("http://service1.example.com"))
                .route("service2_route", r -> r
                        .path("/service2/**")
                        .filters(f -> f
                                .requestRateLimiter(config -> config.setKeyResolver(redisKeyResolver()))
                                .addResponseHeader(HttpHeaders.CACHE_CONTROL, "max-age=3600"))
                        .uri("http://service2.example.com"))
                // Add more routes for other microservices
                .build();
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 2); // Example: allow 1 request per second, with a burst capacity of 2 requests
    }

    @Bean
    public KeyResolver redisKeyResolver() {
        return new PrincipalNameKeyResolver(); // Example key resolver, use appropriate key resolver for your use case
    }
}
