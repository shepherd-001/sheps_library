package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "permissions")
    Optional<UserRole> findByNameEqualsIgnoreCase(String name);
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "permissions")
    java.util.List<UserRole> findByIdIn(java.util.Collection<Long> ids);

    boolean existsByNameEqualsIgnoreCase(String name);

    @Query("select r.name from UserRole  r")
    Set<String> findAllNamesAsSet(); // Avoids loading all entities
}
