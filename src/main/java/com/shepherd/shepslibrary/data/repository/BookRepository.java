package com.shepherd.shepslibrary.data.repository;

import com.shepherd.shepslibrary.data.model.Book;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;

public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Book e where e.id = :id")
    Optional<Book> findByIdForUpdate(@Param("id") UUID id);

    Optional<Book> findByIsbn(String isbn);
    boolean existsByTitle(String title);
    boolean existsById(@Nonnull UUID id);

    @Modifying
    @Query("delete from Book  book where book.id = :id")
    int deleteByIdReturningCount(@Param("id") UUID id);
}
