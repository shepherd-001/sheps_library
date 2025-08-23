package com.shepherd.shepslibrary.auditing;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AuditEvent extends ApplicationEvent {
    private final AuditLog auditLog;
    public AuditEvent(AuditLog auditLog) {
        super(auditLog);
        this.auditLog = auditLog;
    }
}
