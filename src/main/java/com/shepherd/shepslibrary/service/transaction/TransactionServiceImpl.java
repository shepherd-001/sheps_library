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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public TransactionResponse borrowBook(BorrowBookRequest request) {
        User user = AppUtils.getCurrentUser();
        checkIfUserIsRevoked(user);
        Book book = getBookById(request.getBookId());
        checkIfBookIsAvailable(book);
        validateReturnDate(request.getReturnDate());
        book.setAvailable(false);
        bookRepository.save(book);

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setBorrowDate(LocalDate.now());
        transaction.setReturnDate(request.getReturnDate());
        transaction.setCreatedBy(user.getEmail());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return buildTransactionResponse(savedTransaction);
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
        if (LocalDate.now().plusMonths(2).isAfter(returnDate))
            throw new TransactionException("Return date cannot be more than two months");
    }

    private TransactionResponse buildTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .message("Book borrowed successfully")
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
        Transaction transaction = getTransactionById(transactionId);
        Book book = transaction.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        transaction.setUpdatedBy(AppUtils.getCurrentUser().getEmail());
        transaction.setReturnDate(LocalDate.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return buildTransactionResponse(savedTransaction);
    }

    private Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                ()-> new ShepsLibraryException("Transaction with the provided ID not found"));
    }

    @Override
    public PaginatedResponse<TransactionResponse> getAllTransactions(int pageNumber) {
        return null;
    }
}
