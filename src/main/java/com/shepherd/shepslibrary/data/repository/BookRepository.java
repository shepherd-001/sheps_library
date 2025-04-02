package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Book;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByTitle(String title);
    boolean existsById(@Nonnull UUID id);
}
