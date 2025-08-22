package com.shepherd.shepslibrary.auditing;

import com.shepherd.shepslibrary.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final ApplicationEventPublisher publisher;

    @Async
    public void record(String actor, String method, String endpoint, String params,
                       String ip, String userAgent, int statusCode, long duration){
        AuditLog  auditLog = AuditLog.builder()
                .actor(actor)
                .method(method)
                .endpoint(endpoint)
                .params(params)
                .ipAddress(ip)
                .userAgent(userAgent)
                .statusCode(statusCode)
                .timeStamp(Instant.now())
                .duration(duration)
                .build();
        auditLogRepository.save(auditLog);

        // public for alerting
        publisher.publishEvent(new AuditEvent(auditLog));
    }
}
