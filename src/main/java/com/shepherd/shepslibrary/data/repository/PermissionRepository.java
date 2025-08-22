package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByNameEqualsIgnoreCase(String name);
    boolean existsByNameEqualsIgnoreCase(String name);

    @Query("select p.name from Permission p")
    Set<String> findAllNamesAsSet(); // Avoids loading full entities
}
