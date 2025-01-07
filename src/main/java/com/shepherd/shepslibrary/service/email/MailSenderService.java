package com.shepherd.shepslibrary.service.email;

public interface MailSenderService {
    void sendEmail(String to, String subject, String htmlContent);
}
