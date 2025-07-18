package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Page<Transaction> findAllByUserId(String userId, Pageable pageable);

//    @Query("""
//            select t from Transaction t
//            where t.returnDate < :today
//            order by t.returnDate
//           """)
//    Page<Transaction> findOverdueTransactions(LocalDate today, Pageable pageable);
}
