package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.service.userRoleAndPermission.permission.PermissionCatalog;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** Enforces policy for HTTP callers and any other service entry point. */
@Component
public class AuthorizationPolicy {
    public record Actor(UUID userId, String roleName, Set<String> permissions) {}

    public Actor require(String capability) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)
                || principal.getUser().getId() == null || principal.getUser().getRole() == null
                || !principal.getUser().isEnabled() || !principal.getUser().isEmailVerified()) {
            throw new AccessDeniedException("An authenticated, enabled user is required");
        }
        Set<String> capabilities = authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority()).filter(PermissionCatalog.codes()::contains)
                .collect(Collectors.toUnmodifiableSet());
        if (!capabilities.contains(capability)) throw new AccessDeniedException("Missing permission: " + capability);
        return new Actor(principal.getUser().getId(), principal.getUser().getRole().getName(), capabilities);
    }

    public void checkTarget(Actor actor, String roleName) {
        if (RoleNames.SYSTEM_ROLES.contains(roleName)) {
            throw new AccessDeniedException("System roles are managed by deployment migrations");
        }
        if (roleName.equalsIgnoreCase(actor.roleName())) {
            throw new AccessDeniedException("You cannot change or delete your own role");
        }
    }

    public void checkGrant(Actor actor, Set<String> permissions) {
        if (!actor.permissions().containsAll(permissions)) {
            throw new AccessDeniedException("You cannot grant permissions you do not hold");
        }
        if (!"ADMIN".equals(actor.roleName()) && permissions.stream()
                .map(PermissionCatalog::fromCode).anyMatch(PermissionCatalog::administrative)) {
            throw new AccessDeniedException("Only ADMIN may delegate role administration");
        }
    }
}
