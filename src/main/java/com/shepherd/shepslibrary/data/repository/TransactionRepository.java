package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Transaction e where e.id = :id")
    Optional<Transaction> findByIdForUpdate(@Param("id") UUID id);

    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"user", "book"})
    Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);
    @Override
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"user", "book"})
    Page<Transaction> findAll(Pageable pageable);
}
