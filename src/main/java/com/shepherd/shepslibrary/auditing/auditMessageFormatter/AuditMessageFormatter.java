package com.shepherd.shepslibrary.auditing.auditMessageFormatter;

import com.shepherd.shepslibrary.auditing.AuditLog;

public interface AuditMessageFormatter {
    String format(AuditLog auditLog);
}