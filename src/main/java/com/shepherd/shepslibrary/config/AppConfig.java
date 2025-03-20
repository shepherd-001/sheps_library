package com.shepherd.shepslibrary.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.concurrent.*;

@Configuration
public class AppConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService(
            @Value("${executor.corePoolSize}") int corePoolSize,
            @Value("${executor.maxPoolSize}") int maxPoolSize,
            @Value("${executor.keepAliveTime}") long keepAliveTime) {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
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
