package com.shepherd.shepslibrary.service.admin;

import com.shepherd.shepslibrary.data.dto.request.AddRoleRequest;
import com.shepherd.shepslibrary.data.dto.request.AssignPermissionRequest;
import com.shepherd.shepslibrary.data.dto.request.InviteLibrarianRequest;
import com.shepherd.shepslibrary.data.dto.response.InviteLibrarianResponse;
import com.shepherd.shepslibrary.data.model.Gender;
import com.shepherd.shepslibrary.data.model.TokenType;
import com.shepherd.shepslibrary.data.model.User;
import com.shepherd.shepslibrary.data.model.UserRole;
import com.shepherd.shepslibrary.data.repository.UserRepository;
import com.shepherd.shepslibrary.exceptions.AlreadyExistsException;
import com.shepherd.shepslibrary.exceptions.ResourceNotFoundException;
import com.shepherd.shepslibrary.exceptions.ShepsLibraryException;
import com.shepherd.shepslibrary.exceptions.UserAlreadyEnabledException;
import com.shepherd.shepslibrary.mapper.UserMapper;
import com.shepherd.shepslibrary.service.notification.MailNotificationService;
import com.shepherd.shepslibrary.service.token.TokenService;
import com.shepherd.shepslibrary.service.userRoleAndPermission.role.RoleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.shepherd.shepslibrary.utils.RoleUtils.ADMIN;
import static com.shepherd.shepslibrary.utils.RoleUtils.LIBRARIAN;


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
//    private final EmailValidationService emailValidationService;
    private final UserMapper userMapper;
    private final RoleService roleService;


    @Override
    public void createAdminIfNotExists() {
        if(userRepository.existsByRoleName(ADMIN)){
            log.info("Admin already exists");
            return;
        }
        UserRole role = roleService.getRole(ADMIN);
        User admin = User.builder()
                .firstName("ShepLibrary")
                .lastName("Admin")
                .gender(Gender.MALE)
                .email(adminEmail.toLowerCase())
                .password(passwordEncoder.encode(adminPassword))
                .role(role)
                .enabled(true)
                .emailVerified(true)
                .revoked(false)
                .build();

        userRepository.save(admin);
        log.info("Admin created successfully");
    }

    @Override
    @Transactional
    public InviteLibrarianResponse inviteLibrarian(InviteLibrarianRequest request) {
        if(userRepository.existsByEmailIgnoreCase(request.getEmail().trim()))
            throw new AlreadyExistsException("User with the provided email already exists");
//        emailValidationService.checkAndValidateEmail(request.getEmail());

        UserRole role = roleService.getRole(LIBRARIAN);

        User librarian = userMapper.mapToLibrarian(request);
        librarian.setRole(role);

        librarian = userRepository.save(librarian);
        sendLibrarianInvite(librarian);
        return userMapper.mapToInviteLibrarianResponse(librarian);
    }

    private void sendLibrarianInvite(User user) {
        String token = tokenService.generateToken(user, TokenType.LIBRARIAN_INVITATION);
        mailNotificationService.sendLibrarianInvitation(user, token);
        log.info("==>> Invited librarian {} successfully", user.getEmail());
    }

    @Override
//    @Transactional
    public String resendInvite(String inviteeEmail) {
        log.info("==>> Initiating resend invitation for email: {}", inviteeEmail);
        return userRepository.findByEmailIgnoreCase(inviteeEmail.trim())
                .map(this::handleResendInvite)
                .orElseThrow(
                        ()-> new ResourceNotFoundException("User not found. Invitation not resent"));
    }

    @Override
    public String addRole(AddRoleRequest request) {
        roleService.addRole(request.getName());
        return "Role added successfully";
    }

    @Override
    public String deleteRole(String roleName) {
        roleService.deleteRole(roleName);
        return "Role deleted successfully";
    }

    @Override
    public UserRole assignPermissionsToRole(AssignPermissionRequest request) {
        return roleService.assignPermissionsToRole(request.getRoleName(), request.getPermissionNames());
    }

    private String handleResendInvite(User user) {
        if(user.isEnabled()){
            log.error("User with email {} is already enabled", user.getEmail());
            throw new UserAlreadyEnabledException("User is already verified. Resend invitation not applicable");
        }
        if(!LIBRARIAN.equals(user.getRole().getName())){
            log.warn("User does not have the role {}", LIBRARIAN);
            throw new ShepsLibraryException("User is not a librarian. Resend invitation not applicable");
        }

        sendLibrarianInvite(user);
        return "Librarian invite has been resent successfully";
    }
}