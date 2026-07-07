package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRoleName(String roleName);

    // this is to fetch the user role eagerly only during authentication
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailIgnoreCaseWithRole(@Param("email") String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @Query("""
    select u from User u
    where u.role.name = :roleName
    and u.role.name != :excludedRoleName
    """)
    Page<User> findAllByRoleName(String roleName, String excludedRoleName, Pageable pageable);

    @Query("""
    select u from User u
    where u.enabled = :enabled
    and u.role.name != :excludedRoleName
    """)
    Page<User> findAllByEnabled(boolean enabled, String excludedRoleName, Pageable pageable);
}