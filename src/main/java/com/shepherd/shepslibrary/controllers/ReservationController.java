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
    @PreAuthorize("hasAuthority('member.create')")
    public ResponseEntity<ApiResponse<?>> reserveBook(@PathVariable UUID bookId,
                                                      @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse
                .of("Book reserved successfully", reservationService.reserveBook(bookId, authenticatedUser)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('member.read', 'librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getReservationById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.getReservationById(id)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('member.read')")
    public ResponseEntity<ApiResponse<?>> getMyReservations(@AuthenticationPrincipal AuthenticatedUser authenticatedUser,
                                                            @RequestParam(required = false, defaultValue = "1") int page,
                                                            @RequestParam(required = false, defaultValue = "10") int size,
                                                            @RequestParam(required = false) String sort,
                                                            @RequestParam(required = false) String direction) {
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.getAllReservationByUserId(authenticatedUser.getUser().getId(), paginationRequest)));
    }

    @GetMapping("/all/user/{userId}")
    @PreAuthorize("hasAnyAuthority('librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllReservationsByUserId(@PathVariable UUID userId,
                                                                     @RequestParam(required = false, defaultValue = "1") int page,
                                                                     @RequestParam(required = false, defaultValue = "10") int size,
                                                                     @RequestParam(required = false) String sort,
                                                                     @RequestParam(required = false) String direction) {
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.getAllReservationByUserId(userId, paginationRequest)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('librarian.read', 'admin.read')")
    public ResponseEntity<ApiResponse<?>> getAllReservations(@RequestParam(required = false, defaultValue = "1") int page,
                                                             @RequestParam(required = false, defaultValue = "10") int size,
                                                             @RequestParam(required = false) String sort,
                                                             @RequestParam(required = false) String direction) {
        PaginationRequest paginationRequest = PaginationRequest.of(page, size, sort, direction);
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.getAllReservations(paginationRequest)));
    }

    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasAuthority('member.read')")
    public ResponseEntity<ApiResponse<?>> deleteReservation(@PathVariable UUID reservationId,
                                                            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return ResponseEntity.ok(ApiResponse
                .of(reservationService.deleteReservation(reservationId, authenticatedUser)));
    }

    @DeleteMapping("/all")
    @PreAuthorize("hasAuthority('member.delete')")
    public ResponseEntity<ApiResponse<?>> deleteAllReservations(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        reservationService.deleteAllReservation(authenticatedUser.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}