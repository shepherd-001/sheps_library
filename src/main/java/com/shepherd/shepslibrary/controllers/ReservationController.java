package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.service.transaction.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/create")
    public ResponseEntity<Object> reserveBook(@RequestParam UUID bookId){
        return ResponseEntity.ok(reservationService.reserveBook(bookId));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/get")
    public ResponseEntity<Object> getReservationById(@RequestParam UUID reservationId){
        return ResponseEntity.ok(reservationService.getReservationById(reservationId));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/get-all-by-user")
    public ResponseEntity<Object> getAllReservationsByUserId(@RequestParam UUID userId,
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
    public ResponseEntity<Object> deleteReservation(@PathVariable UUID reservationId, @PathVariable UUID userId){
        return ResponseEntity.ok(reservationService.deleteReservation(reservationId, userId));
    }

    @DeleteMapping("/delete/all/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Object> deleteAllReservations(@PathVariable UUID userId){
        return ResponseEntity.ok(reservationService.deleteAllReservation(userId));
    }
}
