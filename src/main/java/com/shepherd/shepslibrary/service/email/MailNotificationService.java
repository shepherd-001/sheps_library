package com.shepherd.shepslibrary.service.email;


import com.shepherd.shepslibrary.data.model.User;

public interface MailNotificationService {
    void sendVerificationMail(User user, String token);
    void sendResetPasswordMail(User user, String token);
}
