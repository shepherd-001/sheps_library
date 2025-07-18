package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
import com.shepherd.shepslibrary.service.transaction.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/create/{bookId}")
    public ResponseEntity<ApiResponse<?>> reserveBook(@PathVariable String bookId){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .buildResponse("Book reserved successfully", reservationService.reserveBook(bookId)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getReservationById(@PathVariable String id){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.getReservationById(id)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/all/{userId}/{page}")
    public ResponseEntity<ApiResponse<?>> getAllReservationsByUserId(@PathVariable String userId, @PathVariable  int page){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.getAllReservationByUserId(userId, page)));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllReservations(@RequestBody PaginationRequest request){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.getAllReservations(request)));
    }

    @DeleteMapping("/{reservationId}/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<?>> deleteReservation(@PathVariable String reservationId, @PathVariable String userId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.deleteReservation(reservationId, userId)));
    }

    @DeleteMapping("/all/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<?>> deleteAllReservations(@PathVariable String userId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.deleteAllReservation(userId)));
    }
}
