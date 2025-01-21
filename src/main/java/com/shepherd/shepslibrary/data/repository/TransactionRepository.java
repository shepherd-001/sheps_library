package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findAllByUserId(UUID userId, Pageable pageable);

    @Query("""
            select t from Transaction t
            where t.returnDate < :today
            order by t.returnDate
           """)
    Page<Transaction> findOverdueTransactions(LocalDate today, Pageable pageable);
}
