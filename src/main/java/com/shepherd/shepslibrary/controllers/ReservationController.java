package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.response.ApiResponse;
import com.shepherd.shepslibrary.data.dto.request.PaginationRequest;
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
    public ResponseEntity<ApiResponse<?>> reserveBook(@RequestParam String bookId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse("Book reserved successfully", reservationService.reserveBook(bookId)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getReservationById(@RequestParam @NotBlank(message = ValidationMessage.NULL_RESERVATION_ID)
                                                                 String reservationId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.getReservationById(reservationId)));
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/all/user-id")
    public ResponseEntity<ApiResponse<?>> getAllReservationsByUserId(@RequestParam @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                                         String userId,
                                                             @RequestParam(defaultValue = "0") int pageNumber){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.getAllReservationByUserId(userId, pageNumber)));
    }

    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<?>> getAllReservations(@RequestBody PaginationRequest request){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.getAllReservations(request)));
    }

    @DeleteMapping("/{reservationId}/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<?>> deleteReservation(@PathVariable @NotBlank(message = ValidationMessage.NULL_RESERVATION_ID)
                                                                String reservationId,
                                                    @PathVariable @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                    String userId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.deleteReservation(reservationId, userId)));
    }

    @DeleteMapping("/all/{userId}")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<ApiResponse<?>> deleteAllReservations(@PathVariable @NotBlank(message = ValidationMessage.NULL_USER_ID)
                                                                    String userId){
        return ResponseEntity.ok(ApiResponse
                .buildResponse(reservationService.deleteAllReservation(userId)));
    }
}
