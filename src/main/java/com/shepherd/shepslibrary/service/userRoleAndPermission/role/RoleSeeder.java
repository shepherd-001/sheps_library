package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.data.model.UserRole;
import com.shepherd.shepslibrary.data.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(1)
@Slf4j
public class RoleSeeder implements CommandLineRunner {
    private final UserRoleRepository userRoleRepository;
    private final AppRolesConfig appRolesConfig;
    private final RoleServiceImpl roleService;


    @Override
    public void run(String... args){
        Set<String> existingRoles = userRoleRepository.findAll()
                .stream()
                .map(role -> role.getName().trim().toUpperCase())
                .collect(Collectors.toSet());

        List<UserRole> newRoles = appRolesConfig.getRoles().stream()
                .filter(roleName -> !existingRoles.contains(roleName.toUpperCase()))
                .map(roleName -> UserRole.builder()
                        .name(roleName.trim().toUpperCase())
                        .build())
                .toList();
        if(!newRoles.isEmpty()) {
            log.info("Inserted new roles into the DB");
            userRoleRepository.saveAll(newRoles);
            roleService.loadRoles();
        }
    }
}