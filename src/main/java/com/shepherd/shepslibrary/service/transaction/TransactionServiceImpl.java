package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.model.Transaction;
import com.shepherd.shepslibrary.data.model.TransactionType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TransactionRepository;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.exceptions.TransactionException;
import com.shepherd.shepslibrary.service.book.BookService;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.utils.AppUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.shepherd.shepslibrary.utils.AppUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;
    private final BookService bookService;
    private final MailNotificationService mailNotificationService;

    @Override
    @Transactional
    public TransactionResponse borrowBook(BorrowBookRequest request) {
        log.info("::::: Initiating borrow book request :::::");
        User user = AppUtils.getCurrentUser();
        checkIfUserIsRevoked(user);
        Book book = bookService.fetchBookById(request.getBookId());
        checkIfBookIsAvailable(book);
        validateReturnDateTime(request.getReturnDateTime());
        book.setAvailable(false);
        Book savedBook = bookService.saveBook(book);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.BORROW_BOOK);
        transaction.setUser(user);
        transaction.setBook(savedBook);
        transaction.setBorrowDateTime(LocalDateTime.now());
        transaction.setReturnDateTime(request.getReturnDateTime());
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("::::: Book borrowed successfully :::::");
        return mapToTransactionResponse(savedTransaction);
    }

    private void checkIfUserIsRevoked(User user){
        if(user.isRevoked())
            throw new ShepsLibraryException("Your access to perform this action has been revoked. " +
                    "Please settle your overdue payment or contact our support team for assistance");
    }

    private void checkIfBookIsAvailable(Book book){
        if(!book.isAvailable())
            throw new TransactionException("Book is not available");
    }

    private void validateReturnDateTime(LocalDateTime returnDateTime) {
        LocalDateTime now = LocalDateTime.now();
        if (returnDateTime.isBefore(now)) {
            throw new TransactionException("Return date cannot be in the past.");
        }
        if (returnDateTime.isAfter(now.plusMonths(MAX_BORROW_MONTHS))) {
            throw new TransactionException("Return date cannot be more than %d months from today.".formatted(MAX_BORROW_MONTHS));
        }
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction){
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .transactionType(transaction.getTransactionType())
                .firstName(transaction.getUser().getFirstName())
                .lastName(transaction.getUser().getLastName())
                .title(transaction.getBook().getTitle())
                .author(transaction.getBook().getAuthor())
                .genre(transaction.getBook().getGenre())
                .borrowedDateTime(transaction.getBorrowDateTime())
                .returnDateTime(transaction.getReturnDateTime())
                .build();
    }

    @Override
    public TransactionResponse returnBook(String transactionId) {
        log.info("::::: Initiating return book :::::");
        Transaction transaction = getTransactionById(transactionId);
        Book book = transaction.getBook();
        book.setAvailable(true);
        bookService.saveBook(book);

        transaction.setTransactionType(TransactionType.RETURN_BOOK);
        transaction.setReturnDateTime(LocalDateTime.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToTransactionResponse(savedTransaction);
    }

    private Transaction getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                ()-> new ShepsLibraryException("Transaction with the provided ID not found"));
    }

    @Override
    @Cacheable(value = "transactionCache", key = "'user:' + #userId + ':page:' + #pageNumber")
    public PaginationResponse<TransactionResponse> getAllTransactionByUserId(String userId, int pageNumber) {
        log.info("::::: Fetching all transactions by user id :::::");
        Pageable pageable = AppUtils.createPageRequest(pageNumber, DEFAULT_PAGE_SIZE, SORT_BY_CREATED_AT, SORT_DIRECTION_ASC);
        Page<Transaction> transactions = transactionRepository.findAllByUserId(userId, pageable);
        return getTransactionPaginatedResponse(transactions);
    }

    private PaginationResponse<TransactionResponse> getTransactionPaginatedResponse(Page<Transaction> transactions){
        return PaginationResponse.<TransactionResponse>builder()
                .content(transactions.stream()
                        .map(this::mapToTransactionResponse)
                        .toList())
                .numberOfElements(transactions.getNumberOfElements())
                .totalPages(transactions.getTotalPages())
                .totalElements(transactions.getTotalElements())
                .isLast(transactions.isLast())
                .build();
    }

    @Override
    @Cacheable(
            value = "transactionCache",
            key = "#paginationRequest.toCacheKey('transactions')",
            unless = "#result == null || #result.content.isEmpty()"
    )
    public PaginationResponse<TransactionResponse> getAllTransactions(PaginationRequest paginationRequest) {
        log.info("::::: Fetching all transactions :::::");
        Pageable pageable = createPageRequest(paginationRequest.getPageNumber(), paginationRequest.getPageSize(),
                paginationRequest.getSortBy(), paginationRequest.getSortDirection());
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return getTransactionPaginatedResponse(transactions);
    }

//    @Override
//    @Scheduled(cron = "0 0 9 * * ?")
//    public void sendBookOverdueNotifications(){
//        Pageable pageable = PageRequest.of(0, 100);
//        try{
//            while (true){
//                Page<Transaction> overdueTransactionsPage = transactionRepository.findOverdueTransactions(LocalDate.now(), pageable);
//                if (overdueTransactionsPage.isEmpty()) {
//                    log.info("::::: No transaction found :::::");
//                    break;
//                }
//                overdueTransactionsPage
//                        .getContent().forEach(mailNotificationService::sendOverdueBookMail);
//                log.info("::::: Processing transaction page number {} :::::", overdueTransactionsPage.getNumber());
//                pageable = pageable.next();
//            }
//        }catch (Exception exception){
//            throw new ShepsLibraryException(exception.getMessage());
//        }
//    }
}
