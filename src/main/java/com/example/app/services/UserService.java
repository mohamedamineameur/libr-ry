package com.example.app.services;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;

import com.example.app.models.UserModel;
import com.example.app.repositories.UserRepository;
import com.example.app.dtos.userDTO.CreateUserRequest;
import com.example.app.dtos.userDTO.UpdateUserRequest;
import com.example.app.dtos.userDTO.UserResponse;
import com.example.app.dtos.userDTO.SetActiveRequest;
import com.example.app.dtos.userDTO.SetAdminRequest;
import com.example.app.dtos.userDTO.ChangePasswordRequest;
import com.example.app.dtos.userDTO.LoginRequest;
import com.example.app.exceptions.BusinessException;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.SessionModel;
import com.example.app.security.ResourceAuthorizationService;
import com.example.app.services.SecurityService.VerifyEmailResult;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;
    private final SessionService sessionService;
    private final SecurityService securityService;
    private final ResourceAuthorizationService resourceAuthorizationService;

    public UserService(
        UserRepository userRepository,
        MessageSource messageSource,
        BCryptPasswordEncoder bCryptPasswordEncoder,
        TokenService tokenService,
        SessionService sessionService,
        SecurityService securityService,
        ResourceAuthorizationService resourceAuthorizationService
    ) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenService = tokenService;
        this.sessionService = sessionService;
        this.securityService = securityService;
        this.resourceAuthorizationService = resourceAuthorizationService;
    }

    //create user and verify if the user already exists by email and if the password and confirm password are the same
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "USER_EMAIL_ALREADY_EXISTS",
                message("user.email.already.exists", "User email already exists.")
            );
        }

        if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "PASSWORD_CONFIRMATION_MISMATCH",
                message("user.password.not.match", "Password and confirm password do not match.")
            );
        }

        UserModel user = new UserModel(createUserRequest.getName(), createUserRequest.getEmail(), bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        UserModel createdUser = userRepository.save(user);
        securityService.createForUser(createdUser);
        securityService.sendEmailVerification(createdUser.getId(), createdUser.getEmail());
        return new UserResponse(createdUser);
    }

    //update user and verify if the user exists by id
    public UserResponse updateUser(UpdateUserRequest updateUserRequest) {
        if (updateUserRequest.getId() == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "MISSING_USER_ID", "User id is required");
        }
        resourceAuthorizationService.assertOwnerOrAdmin(updateUserRequest.getId());

        try {
            UserModel updated = userRepository.updateUser(
                updateUserRequest.getId(),
                updateUserRequest.getName(),
                updateUserRequest.getEmail()
            );
            return new UserResponse(updated);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "USER_NOT_FOUND",
                message("user.not.found", "User not found.")
            );
        }
    }

    //change password and verify if the user exists by id and if the old password is correct by comparing hashed passwords
    public UserResponse changePassword(UUID id, ChangePasswordRequest changePasswordRequest) {
        resourceAuthorizationService.assertOwnerOrAdmin(id);

        UserModel user;
        try {
            user = userRepository.findById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "USER_NOT_FOUND",
                message("user.not.found", "User not found.")
            );
        }

        if (!bCryptPasswordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "PASSWORD_INCORRECT",
                message("user.password.incorrect", "Current password is incorrect.")
            );
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "PASSWORD_CONFIRMATION_MISMATCH",
                message("user.password.not.match", "Password and confirm password do not match.")
            );
        }

        UserModel updated = userRepository.changePassword(
            id,
            user.getPassword(),
            bCryptPasswordEncoder.encode(changePasswordRequest.getNewPassword()),
            bCryptPasswordEncoder.encode(changePasswordRequest.getConfirmPassword())
        );
        return new UserResponse(updated);
    }

    //set active and verify if the user exists by id
    public UserResponse setActive(SetActiveRequest setActiveRequest) {
        try {
            UserModel updated = userRepository.setActive(setActiveRequest.getUserId(), setActiveRequest.getIsActive());
            return new UserResponse(updated);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "USER_NOT_FOUND",
                message("user.not.found", "User not found.")
            );
        }
    }

    //set admin and verify if the user exists by id
    public UserResponse setAdmin(SetAdminRequest setAdminRequest) {
        try {
            UserModel updated = userRepository.setAdmin(setAdminRequest.getUserId(), setAdminRequest.getIsAdmin());
            return new UserResponse(updated);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "USER_NOT_FOUND",
                message("user.not.found", "User not found.")
            );
        }
    }

    //login and verify if the user exists by email and if the password is correct by comparing hashed passwords and send cookie with JWT token
    public String login(LoginRequest loginRequest, String ipAddress, String userAgent) {
        UserModel user;
        try {
            user = userRepository.findByEmail(loginRequest.getEmail());
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "USER_NOT_FOUND",
                message("user.not.found", "User not found.")
            );
        }

        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "PASSWORD_INCORRECT",
                message("user.password.incorrect", "Current password is incorrect.")
            );
        }

        if (!securityService.isMailVerified(user.getId())) {
            securityService.sendEmailVerification(user.getId(), user.getEmail());
            throw new BusinessException(
                HttpStatus.FORBIDDEN,
                "EMAIL_NOT_VERIFIED",
                message("user.email.not.verified", "Email is not verified.")
            );
        }

        if (securityService.is2FAEnabled(user.getId())) {
            securityService.generateAndSendOtp(user.getId(), user.getEmail());
            return "2FA_REQUIRED:" + tokenService.generateLoginChallengeToken(user.getId());
        }

        SessionModel session = sessionService.createSession(user, ipAddress, userAgent);
        return tokenService.generateToken(session.getId());
    }

    public String verifyOtpAndLogin(String loginChallengeToken, String otp, String ipAddress, String userAgent) {
        UUID userId = tokenService.extractLoginChallengeUserId(loginChallengeToken);
        if (!securityService.is2FAEnabled(userId)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "TWO_FACTOR_NOT_ENABLED", "Two-factor authentication is not enabled");
        }

        UserModel user;
        try {
            user = userRepository.findById(userId);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "USER_NOT_FOUND",
                message("user.not.found", "User not found.")
            );
        }

        securityService.verifyOtp(userId, otp);
        SessionModel session = sessionService.createSession(user, ipAddress, userAgent);
        return tokenService.generateToken(session.getId());
    }

    public VerifyEmailResult verifyEmail(UUID userId, String token) {
        return securityService.verifyEmail(userId, token);
    }

    //get all users
    public List<UserResponse> getAllUsers() {
        List<UserModel> users = userRepository.findAll();
        return users.stream().map(UserResponse::new).collect(Collectors.toList());
    }

    //me
    public UserResponse getMe() {
        UUID id = resourceAuthorizationService.currentUserId();
        UserModel user = userRepository.me(id);
        return new UserResponse(user);
    }

    //delete user by id from params without dto and verify if the user exists by id return void
    public void deleteUser(UUID id) {
        resourceAuthorizationService.assertOwnerOrAdmin(id);

        try {
            userRepository.findById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "USER_NOT_FOUND",
                message("user.not.found", "User not found.")
            );
        }

        userRepository.delete(id);
    }

    @SuppressWarnings("null")
    private @NonNull String message(@NonNull String key, @NonNull String defaultMessage) {
        return Objects.requireNonNullElse(
            messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale()),
            defaultMessage
        );
    }
    
}
