package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.ApiResponse;
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

    @PostMapping("/create/{bookId}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAuthority('member.create')")
    public ResponseEntity<ApiResponse<?>> reserveBook(@PathVariable String bookId){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .success("Book reserved successfully", reservationService.reserveBook(bookId)));
    }
    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAnyAuthority('member.read', 'librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getReservationById(@PathVariable String id){
        return ResponseEntity.ok(ApiResponse
                .success(reservationService.getReservationById(id)));
    }

    @GetMapping("/all/{userId}/{page}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAnyAuthority('member.read', 'librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllReservationsByUserId(@PathVariable String userId, @PathVariable  int page){
        return ResponseEntity.ok(ApiResponse
                .success(reservationService.getAllReservationByUserId(userId, page)));
    }

    @GetMapping("/all")
//    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @PreAuthorize("hasAnyAuthority('librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllReservations(@RequestBody PaginationRequest request){
        return ResponseEntity.ok(ApiResponse
                .success(reservationService.getAllReservations(request)));
    }

    @DeleteMapping("/{reservationId}/{userId}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAuthority('member.read')")
    public ResponseEntity<ApiResponse<?>> deleteReservation(@PathVariable String reservationId, @PathVariable String userId){
        return ResponseEntity.ok(ApiResponse
                .success(reservationService.deleteReservation(reservationId, userId)));
    }

    @DeleteMapping("/all/{userId}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAuthority('member.delete')")
    public ResponseEntity<ApiResponse<?>> deleteAllReservations(@PathVariable String userId){
        return ResponseEntity.ok(ApiResponse
                .success(reservationService.deleteAllReservation(userId)));
    }
}
