package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Book;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {
    Optional<Book> findByIsbn(String isbn);
    boolean existsByTitle(String title);
    boolean existsById(@Nonnull String id);
}
