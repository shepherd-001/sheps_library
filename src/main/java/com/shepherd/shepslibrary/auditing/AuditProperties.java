//package com.shepherd.shepslibrary.auditing;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import java.time.Duration;
//import java.util.List;
//
//@Getter
//@Setter
//@Component
//@ConfigurationProperties(prefix = "audit")
//public class AuditProperties {
//    private boolean enabled = true;
//    private List<String> excludedPaths = List.of("/actuator/health", "/actuator/prometheus");
//    ;
//    private int maxBodyBytes = 4096;
//    private int maxResponseBytes = 1024;
//    private List<String> captureBodyForMethods = List.of("POST", "PUT", "PATCH");
//
//    private int queueCapacity = 10000;
//    private int batchSize = 200;
//    private long flushIntervalMs = 2000;
//    private boolean dropOnFull = false; // if true, we drop (or write minimal) instead of blocking
//    private int retentionDays = 90;
//    private int consumerThreads = 1;
//    private Duration dbRetryBackoff = Duration.ofSeconds(2);
//
//    private int enqueueOfferTimeoutMs = 200; // time to wait when queue is full (offer)
//    private boolean enablePersistentFallback = true; // fallback to file when DB unavailable
//    private String fallbackDirectory = "audit-fallback";
//    private int maxWriteRetries = 5;
//    private Duration consumerRetryInitialBackoff = Duration.ofSeconds(1);
//    private Duration consumerRetryMaxBackoff = Duration.ofSeconds(30);
//    private boolean enableMetrics = true;
//}