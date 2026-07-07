package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.common.exceptions.ReservationException;
import com.shepherd.shepslibrary.common.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.common.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.PaginationResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.model.Reservation;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.ReservationRepository;
import com.shepherd.shepslibrary.mapper.ReservationMapper;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.service.book.BookService;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import static com.shepherd.shepslibrary.utils.ErrorMessage.RESERVATION_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {
    private final BookService bookService;
    private final ReservationRepository reservationRepository;
    private final MailNotificationService mailNotificationService;
    private final ReservationMapper reservationMapper;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "reservationDateTime");
    private static final String RESERVATION_CACHE = "reservationCache";

    @Override
    public ReservationResponse reserveBook(UUID bookId, AuthenticatedUser authenticatedUser) {
        User user = authenticatedUser.getUser();
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


    private void checkIfBookIsAvailable(Book book) {
        if (book.isAvailable())
            throw new ReservationException("Book is available; no need to reserve");
    }

    @Override
    @Cacheable(value = RESERVATION_CACHE, key = "#reservationId")
    public ReservationResponse getReservationById(UUID reservationId) {
        log.info("::::: Fetching reservation by id :::::");
        return reservationRepository.findById(reservationId)
                .map(reservationMapper::mapToReservationResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    @Override
    @Cacheable(value = RESERVATION_CACHE,
            key = "#paginationRequest.toCacheKey('user:'+#userId)",
            unless = "#result == null || #result.items.isEmpty()")
    public PaginationResponse<ReservationResponse> getAllReservationByUserId(UUID userId, PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable(ALLOWED_SORT_FIELDS);
        Page<Reservation> reservations = reservationRepository.findAllByUserId(userId, pageable);
        log.info("==>> Fetched all user reservations");
        return PaginationResponse.map(reservations, reservationMapper::mapToReservationResponse);
    }

    @Override
    @Cacheable(
            value =RESERVATION_CACHE,
            key = "#paginationRequest.toCacheKey('allReservations')",
            unless = "#result == null || #result.items.isEmpty()"
    )
    public PaginationResponse<ReservationResponse> getAllReservations(PaginationRequest paginationRequest) {
        Pageable pageable = paginationRequest.toPageable(ALLOWED_SORT_FIELDS);
        Page<Reservation> reservations = reservationRepository.findAll(pageable);
        log.info("==>> Fetched all reservations");
        return PaginationResponse.map(reservations, reservationMapper::mapToReservationResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = RESERVATION_CACHE, key = "#reservationId")
    public String deleteReservation(UUID reservationId, AuthenticatedUser authenticatedUser) {
        User user = authenticatedUser.getUser();
        int deletedCount = reservationRepository.deleteByReservationIdAndUserId(reservationId, user.getId());
        if (deletedCount > 0) {
            log.info("==>> Reservation deleted successfully");
            return "Reservation deleted successfully";
        }
        throw new ResourceNotFoundException(RESERVATION_NOT_FOUND);
    }

    @Override
    @Transactional
    @CacheEvict(value = RESERVATION_CACHE, key = "'user:' + #userId")
    public void deleteAllReservation(UUID userId) {
        int deleted = reservationRepository.deleteAllByUserIdReturningCount(userId);
        if (deleted == 0)
            log.warn("==>> Attempted to delete a non-existing reservation");
        else log.info("==>> Reservations deleted successfully");
    }

    //    @Override
//    @Scheduled(cron = "0 0 12 * * ?")
    public void sendAvailableBooksNotification() {
        Pageable pageable = PageRequest.of(0, 100);
        try {
            while (true) {
                Page<Reservation> availableReservationsPage = reservationRepository.findAllAvailableReservations(pageable);
                if (availableReservationsPage.isEmpty()) {
                    log.info("==>> No reservations found");
                    break;
                }
                availableReservationsPage
                        .getContent().forEach(mailNotificationService::sendAvailableReservationMail);
                log.info("==>> Processing transaction page number {}", availableReservationsPage.getNumber());
                pageable = pageable.next();
            }
        } catch (Exception exception) {
            throw new ShepsLibraryException(exception.getMessage());
        }
    }
}