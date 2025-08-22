package com.shepherd.shepslibrary;

import com.shepherd.shepslibrary.auditing.AuditLog;
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
