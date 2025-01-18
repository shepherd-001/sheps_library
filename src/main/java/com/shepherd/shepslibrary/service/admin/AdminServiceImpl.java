package com.shepherd.shepslibrary.service.admin;

import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.data.dto.response.InviteLibrarianResponse;
import com.shepherd.shepslibrary.data.model.Gender;
import com.shepherd.shepslibrary.data.model.Role;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.service.email.MailNotificationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


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

    @PostConstruct
    public void createAdmin() {
        if(!userRepository.existsByRole(Role.ADMIN)){
            User user = new User();
            user.setFirstName("Admin");
            user.setLastName("Admin");
            user.setGender(Gender.MALE);
            user.setEmail(adminEmail.toLowerCase());
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole(Role.ADMIN);
            user.setEnabled(true);
            user.setRevoked(false);
            userRepository.save(user);
            log.info("::::: Admin created successfully :::::");
        }
    }

    @Override
    @Transactional
    public InviteLibrarianResponse inviteLibrarian(InviteLibrarianRequest request) {
        if(userRepository.existsByEmail(request.getEmail()))
            throw new AlreadyExistsException("User with the provided email already exists");

        User user = createUser(request);

        String token = tokenService.generateToken(user, TokenType.LIBRARIAN_INVITATION);
        mailNotificationService.sendLibrarianInvitation(user, token);
        log.info("::::: Librarian invited successfully :::::");
        return InviteLibrarianResponse.builder()
                .message("Librarian invited successfully")
                .librarianId(user.getId())
                .librarianEmail(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .gender(user.getGender())
                .isEnabled(user.isEnabled())
                .isRevoked(user.isRevoked())
                .build();
    }
    private User createUser(InviteLibrarianRequest request){
        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setGender(request.getGender());
        user.setRole(Role.LIBRARIAN);
        return userRepository.save(user);
    }
}
