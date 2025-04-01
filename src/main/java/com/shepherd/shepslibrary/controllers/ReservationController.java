package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.service.transaction.ReservationService;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/create")
    public ResponseEntity<Object> reserveBook(@RequestParam @NotBlank(message = ValidationMessage.BLANK_BOOK_ID)
                                                  String bookId){
        return ResponseEntity.ok(reservationService.reserveBook(bookId));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/get")
    public ResponseEntity<Object> getReservationById(@RequestParam @NotBlank(message = ValidationMessage.NULL_RESERVATION_ID)
                                                         String reservationId){
        return ResponseEntity.ok(reservationService.getReservationById(reservationId));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/get-all-by-user")
    public ResponseEntity<Object> getAllReservationsByUserId(@RequestParam @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                                 String userId,
                                                             @RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(reservationService.getAllReservationByUserId(userId, pageNumber));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @GetMapping("/get-all")
    public ResponseEntity<Object> getAllReservations(@RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(reservationService.getAllReservations(pageNumber));
    }

    @DeleteMapping("/delete/{reservationId}/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Object> deleteReservation(@PathVariable @NotBlank(message = ValidationMessage.NULL_RESERVATION_ID)
                                                        String reservationId,
                                                    @PathVariable @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                    String userId){
        return ResponseEntity.ok(reservationService.deleteReservation(reservationId, userId));
    }

    @DeleteMapping("/delete/all/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Object> deleteAllReservations(@PathVariable @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                            String userId){
        return ResponseEntity.ok(reservationService.deleteAllReservation(userId));
    }
}
