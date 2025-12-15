package com.shepherd.shepslibrary.auditing;

import lombok.Getter;

@Getter
public enum AuditAction {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    LOGIN("LOGIN"),
    LOGOUT("LOGOUT"),
    PASSWORD_RESET("PASSWORD_RESET"),
    CHANGE_PASSWORD("CHANGE_PASSWORD");


    private final String code;

    AuditAction(String code) {
        this.code = code;
    }
}