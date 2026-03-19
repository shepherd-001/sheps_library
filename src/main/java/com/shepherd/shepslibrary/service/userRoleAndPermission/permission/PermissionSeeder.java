package com.shepherd.shepslibrary.service.userRoleAndPermission.permission;

import com.shepherd.shepslibrary.data.model.Permission;
import com.shepherd.shepslibrary.data.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class PermissionSeeder implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;

    public static final List<String> PERMISSION_KEYS = List.of(
            "admin.create", "admin.read", "admin.update", "admin.delete",
            "librarian.create", "librarian.read", "librarian.update", "librarian.delete",
            "member.create", "member.read", "member.update", "member.delete"
    );

    @Override
    public void run(String... args) throws Exception {
        Set<String> existingPermissions = permissionRepository.findAllNamesAsSet();

        List<Permission> permissions = PERMISSION_KEYS.stream()
                .filter(key -> !existingPermissions.contains(key))
                .map(key -> Permission.builder()
                        .name(key)
                        .build())
                .toList();

        if(!permissions.isEmpty()){
            log.info("Seeding {} new permissions", permissions.size());
            permissionRepository.saveAll(permissions);
            permissionService.clearCache();
        }
    }
}
