package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.data.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
    Optional<User> findByEmail(String email);
    Page<User> findAllByRole(Role role, Pageable pageable);
    Page<User> findAllByIsEnabled(boolean enabled, Pageable pageable);

}
