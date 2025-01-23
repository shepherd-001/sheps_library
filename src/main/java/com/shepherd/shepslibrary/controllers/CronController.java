package com.shepherd.shepslibrary.controllers;

import com.shepherd.shepslibrary.controllers.responses.BaseResponse;
import com.shepherd.shepslibrary.service.transaction.ReservationService;
import com.shepherd.shepslibrary.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cron")
@RequiredArgsConstructor
public class CronController {
    private final ReservationService reservationService;
    private final TransactionService transactionService;

    @PostMapping("/run-cron")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> runCron() {
        reservationService.sendAvailableBooksNotification();
        transactionService.sendBookOverdueNotifications();
        return ResponseEntity.ok(BaseResponse
                .buildResponse("Cron ran successfully"));
    }
}
