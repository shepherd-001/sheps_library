package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.service.transaction.ReservationService;
import com.shepherd.shepslibrary.utils.RegexPattern;
import com.shepherd.shepslibrary.utils.ValidationMessage;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
        return ResponseEntity.ok(BaseResponse
                .buildResponse(reservationService.reserveBook(bookId)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/get")
    public ResponseEntity<Object> getReservationById(@RequestParam UUID reservationId){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(reservationService.getReservationById(reservationId)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/get-all-by-user")
    public ResponseEntity<Object> getAllReservationsByUserId(@RequestParam UUID userId,
                                                             @RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(reservationService.getAllReservationByUserId(userId, pageNumber)));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @GetMapping("/get-all")
    public ResponseEntity<Object> getAllReservations(@RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(BaseResponse
                .buildResponse(reservationService.getAllReservations(pageNumber)));
    }

    @DeleteMapping("/delete/{reservationId}/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Object> deleteReservation(@PathVariable UUID reservationId, @PathVariable UUID userId){
        reservationService.deleteReservation(reservationId, userId);
        return ResponseEntity.ok(BaseResponse.buildResponse("Reservation deleted successfully"));
    }

    @DeleteMapping("/delete/all/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<Object> deleteAllReservations(@PathVariable UUID userId){
        reservationService.deleteAllReservation(userId);
        return ResponseEntity.ok(BaseResponse.buildResponse("Successfully deleted all reservations"));
    }
}
