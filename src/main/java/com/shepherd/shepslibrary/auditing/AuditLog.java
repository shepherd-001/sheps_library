package com.shepherd.shepslibrary.auditing;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Table(name = "audit_log",
        indexes = {
                @Index(name = "idx_audit_actor_ts", columnList = "actor, timeStamp"),
                @Index(name = "idx_audit_target_ts", columnList = "targetType, targetId, timeStamp"),
                @Index(name = "idx_audit_action_ts", columnList = "action, timeStamp"),
                @Index(name = "idx_audit_time", columnList = "timeStamp"),
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String traceId; // X-Request-ID or generated
    private String actor; // authenticated username or ANONYMOUS
    private String actorName;

    private String method;
    private String endpoint;

    private String entityType;
    private String entityId;
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private AuditAction auditAction;
    private String customAction;
    private String actionCategory;

    @Column(length = 4000)
    private String paramsJson; // masked request payload (JSON or query params), limited length

    @Column(length = 4000)
    private String beforeJson;

    @Column(length = 4000)
    private String afterJson;


    private String ipAddress;
    private String userAgent;
    private int statusCode;
    private boolean outcomeSuccess;
    private Instant timeStamp;
    private long durationMs;
    @Column(length = 1000)
    private String message; // optional human-friendly message
    //    optionally store response snippet
    @Column(length = 2000)
    private String responseSnippet;
}