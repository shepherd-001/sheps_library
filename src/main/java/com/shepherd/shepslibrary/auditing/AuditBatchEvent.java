//package com.shepherd.shepslibrary.auditing;
//
//import lombok.Getter;
//import org.springframework.context.ApplicationEvent;
//
//import java.util.Collections;
//import java.util.List;
//
//@Getter
//public class AuditBatchEvent extends ApplicationEvent {
//    private final List<AuditLog> auditLogs;
//    public AuditBatchEvent(List<AuditLog> auditLogs) {
//        super(auditLogs);
//        this.auditLogs = Collections.unmodifiableList(auditLogs);
//    }
//}