package com.shepherd.shepslibrary.service.notification;

public interface MailSenderService {
    void sendEmail(String to, String subject, String htmlContent);
}
