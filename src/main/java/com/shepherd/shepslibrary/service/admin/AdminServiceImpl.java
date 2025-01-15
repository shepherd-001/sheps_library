package com.shepherd.shepslibrary.service.admin;

import com.shepherd.shepslibrary.data.dto.response.InviteLibrarianResponse;
import com.shepherd.shepslibrary.data.model.Gender;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.service.email.MailNotificationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    @Value("${admin_email}")
    private String adminEmail;
    @Value("${admin_password}")
    private String adminPassword;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final MailNotificationService mailNotificationService;
    private static final int MAIL_EXPIRATION_TIME_IN_MIN = 10080;

    @PostConstruct
    public void createAdmin() {
        if(!userRepository.existsByRole(Role.ADMIN)){
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setGender(Gender.MALE);
            user.setEmail(adminEmail);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole(Role.ADMIN);
            user.setEnabled(true);
            user.setRevoked(false);
            userRepository.save(user);
            log.info("::::: Admin created successfully :::::");
        }
    }

    @Override
    public InviteLibrarianResponse inviteLibrarian(Set<String> librarianEmails) {
        for(String email: librarianEmails){
            User user = new User();
            user.setEmail(email);
            user.setRole(Role.LIBRARIAN);
            User savedUser = userRepository.save(user);
            String token = tokenService.saveToken(savedUser, TokenType.LIBRARIAN_INVITATION, MAIL_EXPIRATION_TIME_IN_MIN);
            mailNotificationService.sendVerificationMail(savedUser, token);

//            sendInvite
//            save user
        }
        return null;
    }
}
