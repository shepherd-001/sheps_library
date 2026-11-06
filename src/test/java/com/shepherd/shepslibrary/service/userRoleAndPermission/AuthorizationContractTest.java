package com.shepherd.shepslibrary.service.userRoleAndPermission;

import com.shepherd.shepslibrary.controllers.*;
import com.shepherd.shepslibrary.service.userRoleAndPermission.permission.PermissionCatalog;
import com.shepherd.shepslibrary.service.userRoleAndPermission.role.SystemRolePolicy;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

/** Cross-artifact contracts: prevent deployment SQL and endpoint checks drifting from the catalogue. */
class AuthorizationContractTest {
    @Test void migrationProvisionsExactlyTheCodeCatalogueAndSystemPolicies() throws Exception {
        String sql = Files.readString(Path.of("deployment/authorization/V002__resource_permission_catalogue.sql"));
        var catalogue = Pattern.compile("INSERT INTO permissions \\(name\\) VALUES(.*?)ON DUPLICATE", Pattern.DOTALL).matcher(sql);
        assertTrue(catalogue.find());
        assertEquals(PermissionCatalog.codes(), quoted(catalogue.group(1)));
        SystemRolePolicy.defaults().forEach((name, policy) -> {
            var clause = Pattern.compile("WHERE r.name = '" + name + "' AND p.name IN \\(([^)]*)\\)").matcher(sql);
            assertTrue(clause.find(), "Missing migration policy for " + name);
            assertEquals(policy, quoted(clause.group(1)), "Migration policy differs for " + name);
        });
    }

    @Test void everyActiveControllerAuthorityIsInTheCatalogue() {
        for (Class<?> controller : List.of(RoleController.class, AdminController.class, BookController.class,
                ReservationController.class, TransactionController.class, UserController.class)) {
            for (var method : controller.getDeclaredMethods()) {
                PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
                if (annotation == null) continue;
                Set<String> authorities = quoted(annotation.value());
                assertFalse(authorities.isEmpty(), method.toString());
                assertTrue(PermissionCatalog.codes().containsAll(authorities), method + " uses unknown authority " + authorities);
            }
        }
    }

    private Set<String> quoted(String text) {
        return Pattern.compile("'([^']+)'").matcher(text).results()
                .map(match -> match.group(1)).collect(Collectors.toSet());
    }
}
