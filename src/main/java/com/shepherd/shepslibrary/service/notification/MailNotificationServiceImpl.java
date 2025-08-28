package com.shepherd.shepslibrary.service.notification;

import com.shepherd.shepslibrary.data.model.Book;
import com.shepherd.shepslibrary.data.model.Reservation;
import com.shepherd.shepslibrary.data.model.Transaction;
import com.shepherd.shepslibrary.data.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailNotificationServiceImpl implements MailNotificationService {
    private final MailSenderService mailSenderService;
    @Value("${client_url}")
    private String clientUrl;
    private final SpringTemplateEngine templateEngine;
    private final ExecutorService executorService;

    private void sendEmail(String templateName, String subject, String email, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine.process(templateName, context);
            executorService.submit(() -> mailSenderService.sendEmail(email, subject, htmlContent));
        } catch (Exception e) {
            log.error("==>> Failed to send email [{}] to {}: {}", templateName, email, e.getMessage());
        }
    }

    @Override
    public void sendVerificationMail(User user, String token) {
        String verificationLink = "%s/verify?token=%s".formatted(clientUrl, token);
        Map<String, Object> variables = Map.of(
                "firstName", user.getFirstName(),
                "confirmationLink", verificationLink
        );
        sendEmail("email-confirmation", "Confirm Your Email Address", user.getEmail(), variables);
    }

    @Override
    public void sendResetPasswordMail(User user, String token) {
        String resetPasswordLink = "%s/reset-password?token=%s".formatted(clientUrl, token);
        Map<String, Object> variables = Map.of(
                "firstName", user.getFirstName(),
                "resetPasswordLink", resetPasswordLink
        );
        sendEmail("reset-password", "Reset Your Password", user.getEmail(), variables);
    }

    @Override
    public void sendLibrarianInvitation(User user, String token) {
        String invitationLink = "%s/invitation?token=%s".formatted(clientUrl, token);
        Map<String, Object> variables = Map.of(
                "invitationLink", invitationLink,
                "firstName", user.getFirstName()
        );
        sendEmail("librarian-invitation", "Librarian Invitation", user.getEmail(), variables);
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
        sendEmail("overdue-book", "Overdue Book Notification", email, variables);
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
        sendEmail("available-reservation", "Available Book Notification", email, variables);
    }
}
