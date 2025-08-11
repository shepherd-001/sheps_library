package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByNameEqualsIgnoreCase(String name);
    boolean existsByNameEqualsIgnoreCase(String name);
}
