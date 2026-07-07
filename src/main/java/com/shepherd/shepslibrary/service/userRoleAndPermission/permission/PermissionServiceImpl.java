package com.shepherd.shepslibrary.service.userRoleAndPermission.permission;

import com.shepherd.shepslibrary.common.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.common.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.data.model.Permission;
import com.shepherd.shepslibrary.data.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final CacheManager cacheManager;


    @Override
    @Cacheable(value = "permissions", key = "#name.toLowerCase()")
    public Permission getPermission(String name) {
        log.info("Fetching permission '{}' from DB", name);
        return permissionRepository.findByNameEqualsIgnoreCase(name.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
    }

    @Override
    @CachePut(value = "permissions", key = "#name.toLowerCase()")
    public Permission addPermission(String name) {
        name = name.toLowerCase();
        if(permissionRepository.existsByNameEqualsIgnoreCase(name)) {
            throw new AlreadyExistsException(String.format("Permission '%s' already exists", name));
        }

        log.info("Adding permission '{}'", name);
        return permissionRepository.save(
                Permission.builder()
                        .name(name)
                        .build()
        );
    }

    @Override
    @CacheEvict(value = "permissions", key = "#name.toLowerCase()")
    public void deletePermission(String name) {
        Permission permission = getPermission(name);
        permissionRepository.delete(permission);
        log.info("Deleted permission '{}'", name);
    }

    @Override
    public void clearCache() {
        Cache cache = cacheManager.getCache("permissions");
        if(cache != null) {
            cache.clear();
            log.info("Cache cleared");
        }
    }


//    private final PermissionRepository permissionRepository;
//    private final Map<String, Permission> permissionCache = new ConcurrentHashMap<>();
//
//    @PostConstruct
//    public synchronized void loadPermissions() {
//        permissionCache.clear();
//        permissionRepository.findAll().forEach(permission -> permissionCache.put(permission.getName(), permission));
//    }
//
//    public synchronized void addPermission(String name){
//        name = normalizePermission(name);
//        if(permissionCache.containsKey(name)){
//            throw new AlreadyExistsException(String.format("Permission '%s' already exists", name));
//        }
//        permissionRepository.save(Permission.builder()
//                        .name(name)
//                .build());
//        loadPermissions();
//        log.info("Added permission '{}'", name);
//    }
//
//    public synchronized void deletePermission(String name){
//        if(!isValidPermission(name)){
//            log.info("Permission '{}' not found", name);
//            throw new ResourceNotFoundException("Permission not found");
//        }
//        Permission permission = permissionCache.get(normalizePermission(name));
//        if(permission == null){
//            log.info("Permission '{}' not found", name);
//            throw new ResourceNotFoundException("Permission not found");
//        }
//
//        permissionRepository.delete(permission);
//        loadPermissions();
//        log.info("Deleted permission '{}'", name);
//    }
//
//    public Permission getPermission(String name){
//        if(!isValidPermission(name)){
//            log.info("Permission '{}' does not exist", name);
//            throw new ResourceNotFoundException("Permission is invalid");
//        }
//        return permissionCache.get(normalizePermission(name));
//    }
//
//    private boolean isValidPermission(String name){
//        return permissionCache.containsKey(normalizePermission(name));
//    }
//
//    private String normalizePermission(String name){
//        if(name.isBlank())
//            throw new IllegalArgumentException("Permission name is required");
//        return name.trim().toLowerCase();
//    }
}
