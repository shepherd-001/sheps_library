package com.shepherd.shepslibrary.service.notification;

import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.model.Reservation;
import com.shepherd.shepslibrary.data.model.Transaction;
import com.shepherd.shepslibrary.data.model.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailNotificationServiceImpl implements MailNotificationService {
    private final MailAsyncExecutor mailAsyncExecutor;
    @Value("${client_url}")
    private String clientUrl;


    @Override
    public void sendSuperAdminInvite(User user, String token) {
        String verificationLink = "%s/verify?token=%s".formatted(clientUrl, token);
        Map<String, Object> variables = Map.of(
                "displayName", user.getFirstName(),
                "invitationLink", verificationLink
        );
        mailAsyncExecutor.sendEmailAsync("superadmin-invite", "You're Shep library's Very First User!",
                user.getEmail(), variables);
    }

    @Override
    public void sendVerificationMail(User user, String token) {
        String verificationLink = "%s/verify?token=%s".formatted(clientUrl, token);
        Map<String, Object> variables = Map.of(
                "displayName", user.getFirstName(),
                "confirmationLink", verificationLink
        );
        mailAsyncExecutor.sendEmailAsync("email-confirmation", "Confirm Your Email Address", user.getEmail(), variables);
    }

    @Override
    public void sendResetPasswordMail(User user, String token) {
        String resetPasswordLink = "%s/reset-password?token=%s".formatted(clientUrl, token);
        Map<String, Object> variables = Map.of(
                "firstName", user.getFirstName(),
                "resetPasswordLink", resetPasswordLink
        );
        mailAsyncExecutor.sendEmailAsync("reset-password", "Reset Your Password", user.getEmail(), variables);
    }

    @Override
    public void sendLibrarianInvitation(User user, String token) {
        String invitationLink = "%s/invitation?token=%s".formatted(clientUrl, token);
        Map<String, Object> variables = Map.of(
                "invitationLink", invitationLink,
                "firstName", user.getFirstName()
        );
        mailAsyncExecutor.sendEmailAsync("librarian-invitation", "Librarian Invitation", user.getEmail(), variables);
    }

    @Override
    public void sendOverdueBookMail(Transaction transaction) {
        User user = transaction.getUser();
        String firstName = user.getFirstName();
        String email = user.getEmail();
        Book book = transaction.getBook();
        String bookTitle = book.getTitle();
        String bookAuthor = book.getAuthor();
        Instant borrowDateTime = transaction.getBorrowDateTime();
        Instant dueDateTime = transaction.getReturnDateTime();

        long overdueDays = ChronoUnit.DAYS.between(dueDateTime, LocalDateTime.now());

        Map<String, Object> variables = Map.of(
                "firstName", firstName,
                "overdueDays", overdueDays,
                "bookTitle", bookTitle,
                "bookAuthor", bookAuthor,
                "borrowedDate", borrowDateTime,
                "dueDate", dueDateTime
        );
        mailAsyncExecutor.sendEmailAsync("overdue-book", "Overdue Book Notification", email, variables);
    }

    @Override
    public void sendAvailableReservationMail(Reservation reservation) {
        User user = reservation.getUser();
        String firstName = user.getFirstName();
        String email = user.getEmail();
        Book book = reservation.getBook();
        String title = book.getTitle();
        String author = book.getAuthor();

        Map<String, Object> variables = Map.of(
                "firstName", firstName,
                "bookTitle", title,
                "bookAuthor", author
        );
        mailAsyncExecutor.sendEmailAsync("available-reservation", "Available Book Notification", email, variables);
    }
}

@Service
@AllArgsConstructor
@Slf4j
class MailAsyncExecutor {
    private final MailSenderService mailSenderService;
    private final SpringTemplateEngine templateEngine;

    @Async("mailTaskExecutor")
    @Retryable(retryFor = {MailException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public void sendEmailAsync(String templateName, String subject, String email, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            Assert.hasText(htmlContent, "Template rendering failed");
            mailSenderService.sendEmail(email, subject, htmlContent);
        } catch (Exception e) {
            log.error("==>> Failed to send email [{}] to {}: {}", templateName, email, e.getMessage(), e);
            throw e;
        }
    }

    @Recover
    public void recover(MailException ex, String templateName, String subject,
                        String email, Map<String, Object> variables) {
        log.error("==>> Email sending permanently failed after retires. Template: {}, Email: {}",
                templateName,
                email,
                ex);

        // Optional strategies:
        // 1. Save to DB for retry later
        // 2. Send to dead-letter queue
        // 3. Alert monitoring system

    }
}