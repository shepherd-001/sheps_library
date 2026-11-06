package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.data.model.Permission;
import com.shepherd.shepslibrary.data.repository.*;
import com.shepherd.shepslibrary.service.userRoleAndPermission.permission.PermissionCatalog;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

/** Read-only verification on each replica. Deployment migrations perform all provisioning. */
@Component
@Order(1)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.authorization.verify-catalogue", havingValue = "true", matchIfMissing = true)
public class AuthorizationCatalogueVerifier implements ApplicationRunner {
    private final PermissionRepository permissions;
    private final UserRoleRepository roles;

    @Override
    @Transactional(readOnly = true)
    public void run(ApplicationArguments args) {
        Set<String> expected = PermissionCatalog.codes();
        Set<String> found = permissions.findByNameIn(expected).stream().map(Permission::getName).collect(Collectors.toSet());
        if (!found.equals(expected)) throw new IllegalStateException("Apply the authorization catalogue migration before starting the application");
        SystemRolePolicy.defaults().forEach((name, policy) -> {
            var role = roles.findByNameEqualsIgnoreCase(name).orElseThrow(
                    () -> new IllegalStateException("Missing system role: " + name));
            Set<String> actual = role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet());
            if (!role.getName().equals(name) || !actual.equals(policy)) {
                throw new IllegalStateException("System role policy differs from the deployment catalogue: " + name);
            }
        });
    }
}
