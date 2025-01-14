package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.model.Transaction;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.BookRepository;
import com.shepherd.shepslibrary.data.repository.TransactionRepository;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.exceptions.TransactionException;
import com.shepherd.shepslibrary.utils.AppUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.shepherd.shepslibrary.utils.AppUtils.NUMBER_OF_ITEMS_PER_PAGE;
import static com.shepherd.shepslibrary.utils.AppUtils.SORT_BY_CREATED_AT;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;
    private static final int MAX_BORROW_MONTHS = 2;

    @Override
    @Transactional
    public TransactionResponse borrowBook(BorrowBookRequest request) {
        log.info("::::: Initiating borrow book request :::::");
        User user = AppUtils.getCurrentUser();
        checkIfUserIsRevoked(user);
        Book book = getBookById(request.getBookId());
        checkIfBookIsAvailable(book);
        validateReturnDate(request.getReturnDate());
        book.setAvailable(false);
        Book savedBook = bookRepository.save(book);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBook(savedBook);
        transaction.setBorrowDate(LocalDate.now());
        transaction.setReturnDate(request.getReturnDate());
        transaction.setCreatedBy(user.getEmail());
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("::::: Book borrowed successfully :::::");
        return mapToTransactionResponse(savedTransaction);
    }

    private void checkIfUserIsRevoked(User user){
        if(user.isRevoked())
            throw new ShepsLibraryException("Your access to borrow books has been revoked. " +
                    "Please settle your overdue payment or contact our support team for assistance");
    }

    private void checkIfBookIsAvailable(Book book){
        if(!book.isAvailable())
            throw new TransactionException("Book is not available");
    }

    private Book getBookById(UUID bookId) {
        return bookRepository.findById(bookId).orElseThrow
                (()-> new ResourceNotFoundException("Book with the provided ID not found"));
    }

    private void validateReturnDate(LocalDate returnDate) {
        if (LocalDate.now().plusMonths(MAX_BORROW_MONTHS).isAfter(returnDate)) {
            throw new TransactionException("Return date cannot be more than %s months".formatted(MAX_BORROW_MONTHS));
        }
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .firstName(transaction.getUser().getFirstName())
                .lastName(transaction.getUser().getLastName())
                .title(transaction.getBook().getTitle())
                .author(transaction.getBook().getAuthor())
                .genre(transaction.getBook().getGenre())
                .borrowedDate(transaction.getBorrowDate())
                .returnDate(transaction.getReturnDate())
                .build();
    }

    @Override
    public TransactionResponse returnBook(UUID transactionId) {
        log.info("::::: Initiating return book :::::");
        Transaction transaction = getTransactionById(transactionId);
        Book book = transaction.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        transaction.setReturnDate(LocalDate.now());
        transaction.setUpdatedBy(AppUtils.getCurrentUser().getEmail());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    private Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                ()-> new ShepsLibraryException("Transaction with the provided ID not found"));
    }

    @Override
    public PaginatedResponse<TransactionResponse> getAllTransactions(int pageNumber) {
        log.info("::::: Fetching all transactions :::::");
        Pageable pageable = AppUtils.createPageRequest(pageNumber, NUMBER_OF_ITEMS_PER_PAGE, SORT_BY_CREATED_AT, Sort.Direction.ASC);
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return PaginatedResponse.<TransactionResponse>builder()
                .content(transactions.stream()
                        .map(this::mapToTransactionResponse)
                        .toList())
                .numberOfElements(transactions.getNumberOfElements())
                .totalPages(transactions.getTotalPages())
                .totalElements(transactions.getTotalElements())
                .last(transactions.isLast())
                .build();
    }
}
