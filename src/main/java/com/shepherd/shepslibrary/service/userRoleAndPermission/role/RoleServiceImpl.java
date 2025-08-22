package com.shepherd.shepslibrary.service.userRoleAndPermission.role;

import com.shepherd.shepslibrary.data.model.Permission;
import com.shepherd.shepslibrary.data.model.UserRole;
import com.shepherd.shepslibrary.data.repository.UserRoleRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.service.userRoleAndPermission.permission.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final UserRoleRepository userRoleRepository;
    private final PermissionService permissionService;
    private final CacheManager cacheManager;

    @Override
    @Cacheable(value = "roles", key = "#name.toUpperCase()")
    public UserRole getRole(String name){
        log.info("Loading role '{}' from DB", name);
        return userRoleRepository.findByNameEqualsIgnoreCase(name).orElseThrow(
                ()-> new ResourceNotFoundException("Role not found"));
    }

    @Override
    @CachePut(value = "roles", key = "#name.toUpperCase()")
    public UserRole addRole(String name){
        if(userRoleRepository.existsByNameEqualsIgnoreCase(name))
            throw new AlreadyExistsException(String.format("Role '%s' already exists", name));

        log.info("Added role '{}'", name);
        return userRoleRepository.save(UserRole.builder()
                        .name(name.toUpperCase())
                .build());
    }

    @Override
    @CacheEvict(value = "roles", key = "#name.toUpperCase()")
    public void deleteRole(String name){
        UserRole role = getRole(name);
        userRoleRepository.delete(role);
        log.info("Deleted role '{}'", name);
    }

    @Override
    public UserRole assignPermissionsToRole(String name, List<String> permissionNames) {
        UserRole role = getRole(name);

        Set<Permission> permissions = permissionNames.stream()
                .map(permissionService::getPermission)
                .collect(Collectors.toSet());

        role.setPermissions(permissions);
        UserRole updatedRole = userRoleRepository.save(role);
        log.info("Assigned {} permissions to role '{}'", permissions.size(), role.getName());
        return updatedRole;
    }

    @Override
    public void updateCache(UserRole role) {
        Cache cache = cacheManager.getCache("roles");
        if(cache != null) {
            cache.put(role.getName().toUpperCase(), role);
        }
    }

//    private final UserRoleRepository userRoleRepository;
//    private final Map<String, UserRole> roleCache = new ConcurrentHashMap<>();
//
//
//    @PostConstruct
//    public synchronized void loadRoles() {
//        roleCache.clear();
//        userRoleRepository.findAll()
//                .forEach(role -> roleCache.put(role.getName(), role));
//    }
//
//    public synchronized void addRole(String name){
//        if(!isValidRole(name)){
//            throw new AlreadyExistsException(String.format("Role with name '%s' already exists", name.toUpperCase()));
//        }
//        userRoleRepository.save(UserRole.builder()
//                        .name(normalizeRoleName(name))
//                .build());
//        loadRoles();
//        log.info("Added new role '{}'", name);
//    }
//
//    public synchronized void deleteRole(String name) {
//        name = normalizeRoleName(name);
//        UserRole role = roleCache.get(name);
//        if(role == null)
//            throw new ResourceNotFoundException("Role not found");
//        userRoleRepository.delete(role);
//        loadRoles();
//        log.info("Deleted role '{}'", name);
//    }
//
//    public UserRole getRole(String name){
//        if(!isValidRole(name))
//            throw new IllegalArgumentException("Role is invalid");
//        return roleCache.get(normalizeRoleName(name));
//    }
//
//    private boolean isValidRole(String name){
//        return roleCache.containsKey(normalizeRoleName(name));
//    }
//
//    private String normalizeRoleName(String name) {
//        if(name.isBlank())
//            throw new IllegalArgumentException("Role name is required");
//        return name.trim().toUpperCase();
//    }
}