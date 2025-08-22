package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.data.model.Permission;
import com.shepherd.shepslibrary.data.model.UserRole;
import com.shepherd.shepslibrary.data.repository.PermissionRepository;
import com.shepherd.shepslibrary.data.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(2)
@Slf4j
public class RoleSeeder implements CommandLineRunner {
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleServiceImpl roleService;

    private static final Map<String, List<String>> ROLE_PERMISSION_MAP = Map.of(
            "ADMIN", List.of(
                    "admin.create", "admin.read", "admin.update", "admin.delete"),
            "LIBRARIAN", List.of(
                    "librarian.create", "librarian.read", "librarian.update", "librarian.delete"
            ),
            "MEMBER", List.of(
                    "member.create", "member.read", "member.update", "member.delete"
            )
    );


    @Override
    public void run(String... args){
//        Loading roles
        Set<String> existingRoles = userRoleRepository.findAllNamesAsSet();

        List<UserRole> newRoles = ROLE_PERMISSION_MAP.keySet().stream()
                .filter(roleName -> !existingRoles.contains(roleName))
                .map(roleName -> UserRole.builder()
                        .name(roleName)
                        .build())
                .toList();

        if(!newRoles.isEmpty()){
            log.info("Seeding {} new roles", newRoles.size());
            userRoleRepository.saveAll(newRoles);
        }

//        Assign permissions to roles (bulk fetch to avoid N+1)
        Map<String, Permission> permissionMap = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(Permission::getName, p -> p));

        ROLE_PERMISSION_MAP.forEach((roleName, permissionKeys) -> {
            UserRole role = userRoleRepository.findByNameEqualsIgnoreCase(roleName)
                    .orElseThrow(()-> new IllegalStateException(String.format("Role '%s' missing after insert", roleName)));

            Set<Permission> permissions = permissionKeys.stream()
                    .map(permissionMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if(!role.getPermissions().equals(permissions)){
                role.setPermissions(permissions);
                userRoleRepository.save(role);
                roleService.updateCache(role);
                log.info("Assigned {} permissions to role '{}'", permissions.size(), roleName);
            }
        });
    }
}