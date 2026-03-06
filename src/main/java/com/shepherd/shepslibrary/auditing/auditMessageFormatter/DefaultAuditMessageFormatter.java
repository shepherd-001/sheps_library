//package com.shepherd.shepslibrary.auditing.auditMessageFormatter;
//
//import com.shepherd.shepslibrary.auditing.AuditAction;
//import com.shepherd.shepslibrary.auditing.AuditLog;
//import org.springframework.stereotype.Component;
//
//
//@Component
//public class DefaultAuditMessageFormatter implements AuditMessageFormatter {
//    @Override
//    public String format(AuditLog auditLog) {
//        String actorName = auditLog.getActorName() != null ? auditLog.getActorName() : auditLog.getActor();
//        AuditAction auditAction = auditLog.getAuditAction();
//        if(auditAction == null){
//            // fallback for custom/manual actions
//            return String.format("%s performed %s on %s",
//                    actorName,
//                    auditLog.getCustomAction().toLowerCase(),
//                    auditLog.getEntityType().toLowerCase());
//        }
//
//        return switch (auditAction) {
//            case LOGIN -> String.format("%s logged in", actorName);
//            case LOGOUT -> String.format("%s logged out", actorName);
//            case CHANGE_PASSWORD -> String.format("%s changed their password", actorName);
//            case PASSWORD_RESET -> String.format("%s reset their password", actorName);
//            case CREATE -> String.format("%s created %s", actorName, auditLog.getEntityType().toLowerCase());
//            case UPDATE -> String.format("%s updated %s", actorName, auditLog.getEntityType().toLowerCase());
//            case DELETE -> String.format("%s deleted %s", actorName, auditLog.getEntityType().toLowerCase());
//            default -> String.format("%s performed %s", actorName, auditAction.name().toLowerCase());
//        };
//    }
//}