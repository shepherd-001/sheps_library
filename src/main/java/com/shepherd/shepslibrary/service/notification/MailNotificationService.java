package com.shepherd.shepslibrary.service.notification;


import com.shepherd.shepslibrary.data.model.User;

public interface MailNotificationService {
    void sendVerificationMail(User user, String token);
    void sendResetPasswordMail(User user, String token);
    void sendLibrarianInvitation(User user, String token);
}
