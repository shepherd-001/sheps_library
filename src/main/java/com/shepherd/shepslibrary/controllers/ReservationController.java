package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.common.request.PaginationRequest;
import com.shepherd.shepslibrary.common.response.ApiResponse;
import com.shepherd.shepslibrary.security.AuthenticatedUser;
import com.shepherd.shepslibrary.service.transaction.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservation")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/create/{bookId}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAuthority('member.create')")
    public ResponseEntity<ApiResponse<?>> reserveBook(@PathVariable UUID bookId,
                                                      @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .of("Book reserved successfully", reservationService.reserveBook(bookId, authenticatedUser)));
    }
    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAnyAuthority('member.read', 'librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getReservationById(@PathVariable UUID id){
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.getReservationById(id)));
    }

    @GetMapping("/all/{userId}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAnyAuthority('member.read', 'librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllReservationsByUserId(@PathVariable UUID userId,
                                                                     @RequestParam(required = false, defaultValue = "1") int page,
                                                                     @RequestParam(required = false, defaultValue = "10") int size,
                                                                     @RequestParam(required = false) String sort,
                                                                     @RequestParam(required = false) String direction){
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.getAllReservationByUserId(userId, paginationRequest)));
    }

    @GetMapping("/all")
//    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @PreAuthorize("hasAnyAuthority('librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllReservations(@RequestBody PaginationRequest request){
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.getAllReservations(request)));
    }

    @DeleteMapping("/{reservationId}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAuthority('member.read')")
    public ResponseEntity<ApiResponse<?>> deleteReservation(@PathVariable UUID reservationId,
                                                            @AuthenticationPrincipal AuthenticatedUser authenticatedUser){
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.deleteReservation(reservationId, authenticatedUser)));
    }

    @DeleteMapping("/all/{userId}")
//    @PreAuthorize("hasRole('MEMBER')")
    @PreAuthorize("hasAuthority('member.delete')")
    public ResponseEntity<ApiResponse<?>> deleteAllReservations(@PathVariable UUID userId){
        reservationService.deleteAllReservation(userId);
        return ResponseEntity.noContent().build();
    }
}