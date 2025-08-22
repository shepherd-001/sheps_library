package com.shepherd.shepslibrary.auditing;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String actor;
    private String method;
    private String endpoint;
    @Column(length = 4000)
    private String params;
    private String ipAddress;
    private String userAgent;
    private int statusCode;
    private Instant timeStamp;
    private long duration;
}
