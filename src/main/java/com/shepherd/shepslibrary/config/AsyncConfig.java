package com.shepherd.shepslibrary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "mailTaskExecutor")
    public Executor mailTaskExecutor(
            @Value("${executor.mail.corePoolSize}") int corePoolSize,
            @Value("${executor.mail.maxPoolSize}") int maxPoolSize,
            @Value("${executor.mail.queueCapacity}") int queueCapacity) {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("mail-exec-");
        executor.initialize();
        return executor;
    }
}