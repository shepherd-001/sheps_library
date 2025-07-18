package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.data.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmailEqualsIgnoreCase(String email);
    boolean existsByRole(Role role);
    Optional<User> findByEmailEqualsIgnoreCase(String email);
    Page<User> findAllByRole(Role role, Pageable pageable);
    Page<User> findAllByIsEnabled(boolean enabled, Pageable pageable);

}
