package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
