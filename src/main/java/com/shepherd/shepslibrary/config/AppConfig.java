package com.shepherd.shepslibrary.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public CacheManager cacheManager(
            @Value("${cache.expiry}") Duration expiry,
            @Value("${cache.maximumSize}") int maximumSize) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(expiry.toMinutes(), TimeUnit.MINUTES)
                .maximumSize(maximumSize));
        return cacheManager;
    }
}
