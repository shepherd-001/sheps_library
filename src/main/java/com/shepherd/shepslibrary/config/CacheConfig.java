package com.shepherd.shepslibrary.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.LoggingCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
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

    @Bean
    public CacheErrorHandler errorHandler(){
        return new LoggingCacheErrorHandler();
    }
}
