package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.common.exceptions.TransactionException;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.model.*;
import com.shepherd.shepslibrary.data.model.enums.TransactionType;
import com.shepherd.shepslibrary.data.repository.*;
import com.shepherd.shepslibrary.mapper.TransactionMapper;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.service.book.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {
    final TransactionRepository transactions = mock(TransactionRepository.class);
    final BookRepository books = mock(BookRepository.class);
    final BookService bookService = mock(BookService.class);
    final TransactionMapper mapper = mock(TransactionMapper.class);
    final TransactionServiceImpl service = new TransactionServiceImpl(transactions, bookService, books, mapper);
    User owner;
    Book book;
    Transaction loan;
    AuthenticatedUser principal;

    @BeforeEach void setup() {
        owner = User.builder().id(UUID.randomUUID()).build();
        principal = AuthenticatedUser.builder().user(owner).build();
        book = Book.builder().id(UUID.randomUUID()).available(false).build();
        loan = Transaction.builder().id(UUID.randomUUID()).book(book).user(owner)
                .transactionType(TransactionType.BORROW_BOOK).build();
    }

    @Test void cannotReturnAnotherUsersLoan() {
        when(transactions.findByIdForUpdate(loan.getId())).thenReturn(Optional.of(loan));
        AuthenticatedUser stranger = AuthenticatedUser.builder()
                .user(User.builder().id(UUID.randomUUID()).build()).build();
        assertThrows(AccessDeniedException.class, () -> service.returnBook(loan.getId(), stranger));
        verifyNoInteractions(books, bookService);
        verify(transactions, never()).save(any());
    }

    @Test void repeatedReturnCannotReleaseBookBorrowedBySomeoneElse() {
        loan.setTransactionType(TransactionType.RETURN_BOOK);
        when(transactions.findByIdForUpdate(loan.getId())).thenReturn(Optional.of(loan));
        assertThrows(TransactionException.class, () -> service.returnBook(loan.getId(), principal));
        verifyNoInteractions(books, bookService);
    }

    @Test void returnUpdatesBookAndLoan() {
        when(transactions.findByIdForUpdate(loan.getId())).thenReturn(Optional.of(loan));
        when(books.findByIdForUpdate(book.getId())).thenReturn(Optional.of(book));
        when(transactions.save(loan)).thenReturn(loan);
        service.returnBook(loan.getId(), principal);
        assertTrue(book.isAvailable());
        assertEquals(TransactionType.RETURN_BOOK, loan.getTransactionType());
        verify(bookService).saveBook(book);
        verify(transactions).save(loan);
    }

    @Test void unavailableBookCannotBeBorrowed() {
        when(books.findByIdForUpdate(book.getId())).thenReturn(Optional.of(book));
        assertThrows(TransactionException.class, () -> service.borrowBook(
                new BorrowBookRequest(book.getId(), Instant.now().plusSeconds(3600)), principal));
        verifyNoInteractions(transactions, bookService);
    }

    @Test void missingReturnDateCannotWriteAnything() {
        book.setAvailable(true);
        when(books.findByIdForUpdate(book.getId())).thenReturn(Optional.of(book));
        assertThrows(TransactionException.class, () -> service.borrowBook(
                new BorrowBookRequest(book.getId(), null), principal));
        assertTrue(book.isAvailable());
        verifyNoInteractions(transactions, bookService);
    }
}
