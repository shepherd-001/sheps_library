package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import com.shepherd.shepslibrary.data.dto.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.model.Reservation;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.ReservationRepository;
import com.shepherd.shepslibrary.exceptions.ReservationException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.mapper.ReservationMapper;
import com.shepherd.shepslibrary.security.SecurityUtils;
import com.shepherd.shepslibrary.service.book.BookService;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.utils.AppUtils;
import com.shepherd.shepslibrary.utils.ErrorMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService{
    private final BookService bookService;
    private final ReservationRepository reservationRepository;
    private final MailNotificationService mailNotificationService;
    private final ReservationMapper reservationMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "reservationDateTime");

    @Override
    public ReservationResponse reserveBook(String bookId) {
        User user = SecurityUtils.getCurrentPrincipal().getUser();
        checkIfUserIsRevoked(user);
        Book book = bookService.fetchBookById(bookId);

        checkIfBookIsAvailable(book);

        if (reservationRepository.existsByUserIdAndBookId(user.getId(), bookId))
            throw new ReservationException("You have already reserved this book");

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setReservationDateTime(Instant.now());

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("==>> Book reserved successfully");
        return reservationMapper.mapToReservationResponse(savedReservation);
    }

    private void checkIfUserIsRevoked(User user){
        if(user.isRevoked())
            throw new ShepsLibraryException("Your access to perform this action has been revoked. " +
                    "Please settle your overdue payment or contact our support team for assistance");
    }

    private void checkIfBookIsAvailable(Book book){
        if(book.isAvailable())
            throw new ReservationException("Book is available; no need to reserve");
    }

    @Override
    @Cacheable(value = "reservationCache", key = "#reservationId")
    public ReservationResponse getReservationById(String reservationId) {
        log.info("::::: Fetching reservation by id :::::");
        return reservationRepository.findById(reservationId)
                .map(reservationMapper::mapToReservationResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Reservation not found"));
    }

    @Override
    @Cacheable(value = "reservationCache",
            key = "#paginationRequest.toCacheKey('user:'+userId)",
            unless = "#result == null || #result.content.isEmpty()")
    public PaginationResponse<ReservationResponse> getAllReservationByUserId(String userId, PaginationRequest paginationRequest) {
        Pageable pageable = AppUtils.createPageRequest(paginationRequest, ALLOWED_SORT_FIELDS);
        Page<Reservation> reservations = reservationRepository.findAllByUserId(userId, pageable);
        log.info("==>> Fetched all user reservations");
        return paginatedReservationResponse(reservations);
    }

    private PaginationResponse<ReservationResponse> paginatedReservationResponse(Page<Reservation> reservations){
        return PaginationResponse.<ReservationResponse>builder()
                .content(reservations.stream()
                        .map(reservationMapper::mapToReservationResponse)
                        .toList())
                .page(reservations.getNumber() + 1)
                .size(reservations.getSize())
                .numberOfElements(reservations.getNumberOfElements())
                .totalElements(reservations.getTotalElements())
                .totalPages(reservations.getTotalPages())
                .hasNext(reservations.hasNext())
                .hasPrevious(reservations.hasPrevious())
                .last(reservations.isLast())
                .build();
    }

    @Override
    @Cacheable(
            value = "reservationCache",
            key = "#paginationRequest.toCacheKey('allReservations')",
            unless = "#result == null || #result.content.isEmpty()"
    )
    public PaginationResponse<ReservationResponse> getAllReservations(PaginationRequest paginationRequest) {
        Pageable pageable = AppUtils.createPageRequest(paginationRequest, ALLOWED_SORT_FIELDS);
        Page<Reservation> reservations = reservationRepository.findAll(pageable);
        log.info("==>> Fetched all reservations");
        return paginatedReservationResponse(reservations);
    }

    @Override
    @Transactional
    @CacheEvict(value = "reservationCache", key = "#reservationId")
    public String deleteReservation(String reservationId, String userId) {
        int deletedCount = reservationRepository.deleteByReservationIdAndUserId(reservationId, userId);
        if(deletedCount > 0){
            log.info("==>> Reservation deleted successfully");
            return "Reservation deleted successfully";
        }
        if(!reservationRepository.existsById(reservationId))
            throw new ResourceNotFoundException(ErrorMessage.RESERVATION_NOT_FOUND);
        throw new AuthorizationDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    @Override
    @Transactional
    @CacheEvict(value = "reservationCache", key = "'user:' + #userId")
    public String deleteAllReservation(String userId) {
        reservationRepository.deleteAllByUserId(userId);
        log.info("==>> Deleted all users reservations");
        return "Successfully deleted all reservations";
    }

//    @Override
//    @Scheduled(cron = "0 0 12 * * ?")
    public void sendAvailableBooksNotification(){
        Pageable pageable = PageRequest.of(0, 100);
        try{
            while (true){
            Page<Reservation> availableReservationsPage = reservationRepository.findAllAvailableReservations(pageable);
            if(availableReservationsPage.isEmpty()){
                log.info("==>> No reservations found");
                break;
            }
            availableReservationsPage
                    .getContent().forEach(mailNotificationService::sendAvailableReservationMail);
                log.info("==>> Processing transaction page number {}", availableReservationsPage.getNumber());
                pageable = pageable.next();
            }
        }catch (Exception exception){
            throw new ShepsLibraryException(exception.getMessage());
        }
    }
}
