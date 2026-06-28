package com.shepherd.shepslibrary.service.notification;

import com.shepherd.shepslibrary.common.exceptions.MailSenderException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;


@Service
@RequiredArgsConstructor
@Slf4j
public class MailSenderServiceImpl implements MailSenderService {
    private final JavaMailSender mailSender;
    @Value("${mail_from_email}")
    private String mailFromEmail;
    @Value("${mail_from_name}")
    private String mailFromName;


    @Override
    public void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(new InternetAddress(mailFromEmail, mailFromName));
            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);

            log.info("==>> Email notification sent to {}", to);
        } catch (UnsupportedEncodingException | MessagingException ex) {
            log.error("==>> Unable to send email to '{}'.  Error: '{}'", to, ex.getMessage());
            throw new MailSenderException("Unable to send mail");
        }
    }
}