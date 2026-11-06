package com.shepherd.shepslibrary.service.userRoleAndPermission.audit;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/** Append-only through the application API; restrict database UPDATE/DELETE grants operationally. */
@Entity
@Table(name = "authorization_audit", indexes = @Index(name = "idx_authorization_audit_role_time", columnList = "role_name,occurred_at"))
@Immutable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorizationAudit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "actor_id", nullable = false, updatable = false)
    private UUID actorId;
    @Column(nullable = false, updatable = false, length = 40)
    private String action;
    @Column(name = "role_id", nullable = false, updatable = false)
    private Long roleId;
    @Column(name = "role_name", nullable = false, updatable = false, length = 50)
    private String roleName;
    @Column(name = "previous_version", updatable = false)
    private Long previousVersion;
    @Column(name = "resulting_version", updatable = false)
    private Long resultingVersion;
    @Column(name = "before_permissions", nullable = false, updatable = false, columnDefinition = "text")
    private String beforePermissions;
    @Column(name = "after_permissions", nullable = false, updatable = false, columnDefinition = "text")
    private String afterPermissions;
    @Column(nullable = false, updatable = false, length = 500)
    private String reason;
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    public AuthorizationAudit(UUID actorId, String action, Long roleId, String roleName,
                              Long previousVersion, Long resultingVersion, Set<String> before, Set<String> after, String reason) {
        this.actorId = actorId;
        this.action = action;
        this.roleId = roleId;
        this.roleName = roleName;
        this.previousVersion = previousVersion;
        this.resultingVersion = resultingVersion;
        // Catalogue codes cannot contain newlines; sorted newline-separated values are unambiguous.
        this.beforePermissions = String.join("\n", new java.util.TreeSet<>(before));
        this.afterPermissions = String.join("\n", new java.util.TreeSet<>(after));
        this.reason = reason;
        this.occurredAt = Instant.now();
    }
}
