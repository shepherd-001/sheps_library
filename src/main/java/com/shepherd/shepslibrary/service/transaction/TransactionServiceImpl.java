package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.request.BorrowBookRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
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
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

import static com.shepherd.shepslibrary.utils.AppUtils.NUMBER_OF_ITEMS_PER_PAGE;
import static com.shepherd.shepslibrary.utils.AppUtils.SORT_BY_CREATED_AT;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{
    private final TransactionRepository transactionRepository;
    private final BookService bookService;
    private static final int MAX_BORROW_MONTHS = 2;
    private final MailNotificationService mailNotificationService;

    @Override
    @Transactional
    public BaseResponse<TransactionResponse> borrowBook(BorrowBookRequest request) {
        log.info("::::: Initiating borrow book request :::::");
        User user = AppUtils.getCurrentUser();
        checkIfUserIsRevoked(user);
        Book book = bookService.fetchBookById(request.getBookId());
        checkIfBookIsAvailable(book);
        validateReturnDate(request.getReturnDate());
        book.setAvailable(false);
        Book savedBook = bookService.saveBook(book);
        updateBookCache(savedBook);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.BORROW_BOOK);
        transaction.setUser(user);
        transaction.setBook(savedBook);
        transaction.setBorrowDate(LocalDate.now());
        transaction.setReturnDate(request.getReturnDate());
        transaction.setCreatedBy(user.getEmail());
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("::::: Book borrowed successfully :::::");
        return BaseResponse.buildResponse(mapToTransactionResponse(savedTransaction));
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

    private void validateReturnDate(LocalDate returnDate) {
        if (LocalDate.now().plusMonths(MAX_BORROW_MONTHS).isBefore(returnDate)) {
            throw new TransactionException("Return date cannot be more than %s months".formatted(MAX_BORROW_MONTHS));
        }
    }

    @CachePut(value = "bookCache", key = "#book.id")
    public void updateBookCache(Book book) {
        log.info("::::: Updating cache for book :::::");
    }

    @CachePut(value = "transactionCache", key = "#transaction.id")
    public void updateTransactionCache(Transaction transaction) {
        log.info("::::: Updating cache for transaction :::::");
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
                .borrowedDate(transaction.getBorrowDate())
                .returnDate(transaction.getReturnDate())
                .build();
    }

    @Override
    public BaseResponse<TransactionResponse> returnBook(UUID transactionId) {
        log.info("::::: Initiating return book :::::");
        Transaction transaction = getTransactionById(transactionId);
        Book book = transaction.getBook();
        book.setAvailable(true);
        book = bookService.saveBook(book);
        updateBookCache(book);

        transaction.setTransactionType(TransactionType.RETURN_BOOK);
        transaction.setReturnDate(LocalDate.now());
        transaction.setUpdatedBy(AppUtils.getCurrentUser().getEmail());
        Transaction savedTransaction = transactionRepository.save(transaction);
        updateTransactionCache(savedTransaction);
        return BaseResponse.buildResponse(mapToTransactionResponse(savedTransaction));
    }

    private Transaction getTransactionById(UUID transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                ()-> new ShepsLibraryException("Transaction with the provided ID not found"));
    }

    @Override
    @Cacheable(value = "transactionCache", key = "'user:' + #userId + ':page:' + #pageNumber")
    public BaseResponse<PaginatedResponse<TransactionResponse>> getAllTransactionByUserId(UUID userId, int pageNumber) {
        log.info("::::: Fetching all transactions by user id :::::");
        Pageable pageable = buildPageable(pageNumber);
        Page<Transaction> transactions = transactionRepository.findAllByUserId(userId, pageable);
        return BaseResponse.buildResponse(getTransactionPaginatedResponse(transactions));
    }

    private Pageable buildPageable(int pageNumber){
        return AppUtils.createPageRequest(pageNumber, NUMBER_OF_ITEMS_PER_PAGE, SORT_BY_CREATED_AT, Sort.Direction.ASC);
    }
    
    private PaginatedResponse<TransactionResponse> getTransactionPaginatedResponse(Page<Transaction> transactions){
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

    @Override
    public BaseResponse<PaginatedResponse<TransactionResponse>> getAllTransactions(int pageNumber) {
        log.info("::::: Fetching all transactions :::::");
        Pageable pageable = buildPageable(pageNumber);
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return BaseResponse.buildResponse(getTransactionPaginatedResponse(transactions));
    }

//    @Override
//    @Scheduled(cron = "0 0 9 * * ?")
    public void sendBookOverdueNotifications(){
        Pageable pageable = PageRequest.of(0, 100);
        try{
            while (true){
                Page<Transaction> overdueTransactionsPage = transactionRepository.findOverdueTransactions(LocalDate.now(), pageable);
                if (overdueTransactionsPage.isEmpty()) {
                    log.info("::::: No transaction found :::::");
                    break;
                }
                overdueTransactionsPage
                        .getContent().forEach(mailNotificationService::sendOverdueBookMail);
                log.info("::::: Processing transaction page number {} :::::", overdueTransactionsPage.getNumber());
                pageable = pageable.next();
            }
        }catch (Exception exception){
            throw new ShepsLibraryException(exception.getMessage());
        }
    }
}
