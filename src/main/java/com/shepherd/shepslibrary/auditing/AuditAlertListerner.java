package com.shepherd.shepslibrary.auditing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AuditAlertListerner {

    @EventListener
    public void handleAudit(AuditEvent auditEvent) {
        AuditLog auditLog = auditEvent.getAuditLog();

        if("LOGIN_FAILED".equalsIgnoreCase(auditLog.getEndpoint())){
            // Example: send alert if repeated failures detected
            log.error("==>> ALERT: Failed login detected for user {}", auditLog.getActor());
            // Integrate with Slack, Email, PagerDuty etc.

        }
    }
}
