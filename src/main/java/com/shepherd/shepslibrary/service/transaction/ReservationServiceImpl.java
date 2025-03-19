package com.shepherd.shepslibrary.service.transaction;

import com.shepherd.shepslibrary.controllers.response.BaseResponse;
import com.shepherd.shepslibrary.data.dto.response.PaginatedResponse;
import com.shepherd.shepslibrary.data.dto.response.ReservationResponse;
import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.model.Reservation;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.ReservationRepository;
import com.shepherd.shepslibrary.exceptions.ReservationException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.service.book.BookService;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.utils.AppUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
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
public class ReservationServiceImpl implements ReservationService{
    private final BookService bookService;
    private final ReservationRepository reservationRepository;
    private final MailNotificationService mailNotificationService;

    @Override
    public BaseResponse<ReservationResponse> reserveBook(UUID bookId) {
        log.info("::::: Initiating the reservation of book :::::");
        User user = AppUtils.getCurrentUser();
        checkIfUserIsRevoked(user);
        Book book = bookService.fetchBookById(bookId);

        checkIfBookIsAvailable(book);

        if (reservationRepository.existsByUserIdAndBookId(user.getId(), bookId))
            throw new ReservationException("You have already reserved this book");

        Reservation reservation = new Reservation();
        reservation.setBook(book);
        reservation.setUser(user);
        reservation.setReservationDate(LocalDate.now());
        reservation.setCreatedBy(user.getEmail());

        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("::::: Book reserved successfully :::::");
        ReservationResponse reservationResponse = mapToReservationResponse(savedReservation);
        return BaseResponse.buildResponse("Book reserved successfully", reservationResponse);
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
    public BaseResponse<ReservationResponse> getReservationById(UUID reservationId) {
        log.info("::::: Fetching reservation by id :::::");
        return reservationRepository.findById(reservationId)
                .map(reservation -> BaseResponse.
                        buildResponse(mapToReservationResponse(reservation)))
                .orElseThrow(()-> new ResourceNotFoundException("Reservation not found"));
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation){
        return ReservationResponse.builder()
                .userId(reservation.getUser().getId())
                .reservationId(reservation.getId())
                .bookId(reservation.getBook().getId())
                .reservationDate(reservation.getReservationDate())
                .build();
    }

    @Override
    @Cacheable(value = "reservationCache", key = "'user:' + #userId + ':page:' + #pageNumber")
    public BaseResponse<PaginatedResponse<ReservationResponse>> getAllReservationByUserId(UUID userId, int pageNumber) {
        log.info("::::: Fetching all reservations for a user :::::");
        Pageable pageable = buildPageable(pageNumber);
        Page<Reservation> reservations = reservationRepository.findAllByUserId(userId, pageable);
        return BaseResponse.buildResponse(paginatedReservationResponse(reservations));
    }

    private Pageable buildPageable(int pageNumber){
        return AppUtils.createPageRequest(pageNumber, NUMBER_OF_ITEMS_PER_PAGE, SORT_BY_CREATED_AT, Sort.Direction.ASC);
    }

    private PaginatedResponse<ReservationResponse> paginatedReservationResponse(Page<Reservation> reservations){
        return PaginatedResponse.<ReservationResponse>builder()
                .content(reservations.stream()
                        .map(this::mapToReservationResponse)
                        .toList())
                .numberOfElements(reservations.getNumberOfElements())
                .totalElements(reservations.getTotalElements())
                .totalPages(reservations.getTotalPages())
                .last(reservations.isLast())
                .build();
    }

    @Override
    @Cacheable(value = "reservationCache", key = "'allReservations:page:' + #pageNumber")
    public BaseResponse<PaginatedResponse<ReservationResponse>> getAllReservations(int pageNumber) {
        log.info("::::: Fetching all reservations :::::");
        Pageable pageable = buildPageable(pageNumber);
        Page<Reservation> reservations = reservationRepository.findAll(pageable);
        return BaseResponse.buildResponse(paginatedReservationResponse(reservations));
    }

    @Override
    @Transactional
    @CacheEvict(value = "reservationCache", key = "#reservationId")
    public BaseResponse<String> deleteReservation(UUID reservationId, UUID userId) {
        if(!reservationRepository.existsByIdAndUserId(reservationId, userId))
            throw new ResourceNotFoundException("Reservation not found");
        log.info("::::: Initiating the deletion of a user reservation :::::");
        reservationRepository.deleteByIdAndUserId(reservationId, userId);
        log.info("::::: Reservation deleted successfully :::::");
        return BaseResponse.buildResponse("Reservation deleted successfully");
    }

    @Override
    @Transactional
    @CacheEvict(value = "reservationCache", key = "'user:' + #userId")
    public BaseResponse<String> deleteAllReservation(UUID userId) {
        log.info("::::: Initiating the deletion of all user reservations :::::");
        reservationRepository.deleteAllByUserId(userId);
        log.info("::::: Deleted all user reservations :::::");
        return BaseResponse.buildResponse("Successfully deleted all reservations");
    }

//    @Override
//    @Scheduled(cron = "0 0 12 * * ?")
    public void sendAvailableBooksNotification(){
        Pageable pageable = PageRequest.of(0, 100);
        try{
            while (true){
            Page<Reservation> availableReservationsPage = reservationRepository.findAllAvailableReservations(pageable);
            if(availableReservationsPage.isEmpty()){
                log.info("::::: No reservations found :::::");
                break;
            }
            availableReservationsPage
                    .getContent().forEach(mailNotificationService::sendAvailableReservationMail);
                log.info("::::: Processing transaction page number {} :::::", availableReservationsPage.getNumber());
                pageable = pageable.next();
            }
        }catch (Exception exception){
            throw new ShepsLibraryException(exception.getMessage());
        }
    }
}
