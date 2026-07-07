package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.common.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.common.exceptions.TransactionException;
import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.response.TransactionResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.model.Transaction;
import com.shepherd.shepslibrary.data.model.TransactionType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.TransactionRepository;
import com.shepherd.shepslibrary.mapper.TransactionMapper;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.service.book.BookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

import static com.shepherd.shepslibrary.utils.AppUtils.MAX_BORROW_MONTHS;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final BookService bookService;
//    private final MailNotificationService mailNotificationService;
    private final TransactionMapper transactionMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "borrowDateTime", "returnDateTime");
    private static final String TRANSACTION_CACHE = "transactionCache";

    @Override
    @Transactional
    public TransactionResponse borrowBook(BorrowBookRequest request, AuthenticatedUser authenticatedUser) {
        User user = authenticatedUser.getUser();
        Book book = bookService.fetchBookById(request.bookId());
        checkIfBookIsAvailable(book);
        validateReturnDateTime(request.returnDateTime());
        book.setAvailable(false);
        Book savedBook = bookService.saveBook(book);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.BORROW_BOOK);
        transaction.setUser(user);
        transaction.setBook(savedBook);
        transaction.setBorrowDateTime(Instant.now());
        transaction.setReturnDateTime(request.returnDateTime());
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("==>> Book borrowed successfully");
        return transactionMapper.mapToResponse(savedTransaction);
    }

    private void checkIfBookIsAvailable(Book book) {
        if (!book.isAvailable())
            throw new TransactionException("Book is not available");
    }

    private void validateReturnDateTime(Instant returnDateTime) {
        Instant now = Instant.now();
        Instant maxReturnInstant = now.atZone(ZoneId.systemDefault())
                .plusMonths(MAX_BORROW_MONTHS)
                .toInstant();

        if (returnDateTime.isBefore(now)) {
            throw new TransactionException("Return date cannot be in the past.");
        }

        if (returnDateTime.isAfter(maxReturnInstant)) {
            throw new TransactionException("Return date cannot be more than %d months from today.".formatted(MAX_BORROW_MONTHS));
        }
    }

    @Override
    public TransactionResponse returnBook(UUID transactionId) {
        Transaction transaction = getTransactionById(transactionId);
        Book book = transaction.getBook();
        book.setAvailable(true);
        bookService.saveBook(book);

        transaction.setTransactionType(TransactionType.RETURN_BOOK);
        transaction.setReturnDateTime(Instant.now());
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Book returned successfully");
        return transactionMapper.mapToResponse(savedTransaction);
    }

    private Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                () -> new ShepsLibraryException("Transaction with the provided ID not found"));
    }

    @Override
    @Cacheable(value = TRANSACTION_CACHE,
            key = "#paginationRequest.toCacheKey('user:'+#userId)",
            unless = "#result == null || #result.items.isEmpty()")
    public PaginationResponse<TransactionResponse> getAllTransactionByUserId(UUID userId, PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable(ALLOWED_SORT_FIELDS);
        Page<Transaction> transactions = transactionRepository.findAllByUserId(userId, pageable);
        log.info("Fetched all transactions by user id");
        return PaginationResponse.map(transactions, transactionMapper::mapToResponse);
    }

    @Override
    @Cacheable(
            value = TRANSACTION_CACHE,
            key = "#paginationRequest.toCacheKey('transactions')",
            unless = "#result == null || #result.items.isEmpty()"
    )
    public PaginationResponse<TransactionResponse> getAllTransactions(PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable(ALLOWED_SORT_FIELDS);
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        log.info("==>> Fetched all transactions");
        return PaginationResponse.map(transactions, transactionMapper::mapToResponse);
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
