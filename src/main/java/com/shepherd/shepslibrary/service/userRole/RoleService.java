package com.shepherd.shepslibrary.service.userRole;

import com.shepherd.shepslibrary.data.model.UserRole;
import com.shepherd.shepslibrary.data.repository.UserRoleRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final UserRoleRepository userRoleRepository;
    private final Map<String, UserRole> roleCache = new ConcurrentHashMap<>();


    @PostConstruct
    public synchronized void loadRoles() {
        roleCache.clear();
        userRoleRepository.findAll()
                .forEach(role -> roleCache.put(role.getName(), role));
    }

    public synchronized void addRole(String name, String description){
        name = normalizeRoleName(name);
        if(roleCache.containsKey(name))
            throw new AlreadyExistsException(String.format("Role with name '%s' already exists", name));
        userRoleRepository.save(UserRole.builder()
                        .name(name)
                        .description(description)
                .build());
        loadRoles();
        log.info("Added new role '{}'", name);
    }

    public synchronized void deleteRole(String name) {
        name = normalizeRoleName(name);
        UserRole role = roleCache.get(name);
        if(role == null)
            throw new ResourceNotFoundException("Role not found");
        userRoleRepository.delete(role);
        loadRoles();
        log.info("Deleted role '{}'", name);
    }

    public UserRole getRole(String name){
        if(!isValidRole(name))
            throw new ResourceNotFoundException("Role is invalid");
        return roleCache.get(normalizeRoleName(name));
    }

    public boolean isValidRole(String name){
        return roleCache.containsKey(normalizeRoleName(name));
    }

    private String normalizeRoleName(String name) {
        if(name.isBlank())
            throw new IllegalArgumentException("Role name is required");
        return name.trim().toUpperCase();
    }
}