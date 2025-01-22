package com.shepherd.shepslibrary.service.notification;

import com.shepherd.shepslibrary.data.model.Reservation;
import com.shepherd.shepslibrary.data.model.Transaction;
import com.shepherd.shepslibrary.data.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ExecutorService;
@Service
@RequiredArgsConstructor
@Slf4j
public class MailNotificationServiceImpl implements MailNotificationService {
    private final MailSenderService mailSenderService;
    @Value("${client_url}")
    private String baseUrl;
    private final SpringTemplateEngine templateEngine;
    private final ExecutorService executorService;

    private void sendEmail(String templateName, String subject, User user, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(templateName, context);
        log.info("::::: Mail ready to be sent to {} :::::", user.getEmail());
        executorService.submit(() -> mailSenderService.sendEmail(user.getEmail(), subject, htmlContent));
    }

    @Override
    public void sendVerificationMail(User user, String token) {
        String verificationLink = "%s/verify?token=%s".formatted(baseUrl, token);
        Map<String, Object> variables = Map.of(
                "firstName", user.getFirstName(),
                "confirmationLink", verificationLink
        );
        sendEmail("email-confirmation", "Confirm Your Email Address", user, variables);
    }

    @Override
    public void sendResetPasswordMail(User user, String token) {
        String resetPasswordLink = "%s/reset-password?token=%s".formatted(baseUrl, token);
        Map<String, Object> variables = Map.of(
                "firstName", user.getFirstName(),
                "resetPasswordLink", resetPasswordLink
        );
        sendEmail("reset-password", "Reset Your Password", user, variables);
    }

    @Override
    public void sendLibrarianInvitation(User user, String token) {
        String invitationLink = "%s/invitation?token=%s".formatted(baseUrl, token);
        Map<String, Object> variables = Map.of(
                "invitationLink", invitationLink,
                "firstName", user.getFirstName()
        );
        sendEmail("librarian-invitation", "Librarian Invitation", user, variables);
    }

    @Override
    public void sendOverdueBookMail(Transaction transaction) {
        String firstName = transaction.getUser().getFirstName();
        String bookTitle = transaction.getBook().getTitle();
        String bookAuthor = transaction.getBook().getAuthor();
        LocalDate borrowedDate = transaction.getBorrowDate();
        LocalDate dueDate = transaction.getReturnDate();

        long overdueDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());

        Map<String, Object> variables = Map.of(
                "firstName", firstName,
                "overdueDays", overdueDays,
                "bookTitle", bookTitle,
                "bookAuthor", bookAuthor,
                "borrowedDate", borrowedDate,
                "dueDate", dueDate
        );
        sendEmail("overdue-book", "Overdue Book Notification", transaction.getUser(), variables);
    }

    @Override
    public void sendAvailableReservationMail(Reservation reservation) {

    }
}
