package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByRoleName(String roleName);

    // this is to fetch the user role eagerly only during authentication
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailIgnoreCaseWithRole(@Param("email") String email);

    Optional<User> findByEmailIgnoreCase(String email);
    Page<User> findAllByRoleName(String roleName, Pageable pageable);
    Page<User> findAllByEnabled(boolean enabled, Pageable pageable);
}
