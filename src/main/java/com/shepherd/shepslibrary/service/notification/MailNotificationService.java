package com.shepherd.shepslibrary.service.notification;


import com.shepherd.shepslibrary.data.model.Reservation;
import com.shepherd.shepslibrary.data.model.Transaction;
import com.shepherd.shepslibrary.data.model.User;


public interface MailNotificationService {
    void sendVerificationMail(User user, String token);
    void sendResetPasswordMail(User user, String token);
    void sendLibrarianInvitation(User user, String token);
    void sendOverdueBookMail(Transaction transaction);
    void sendAvailableReservationMail(Reservation reservation);
}
