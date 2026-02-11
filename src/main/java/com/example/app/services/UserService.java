package com.example.app.services;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(
        UserRepository userRepository,
        MessageSource messageSource,
        BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        return new UserResponse(createdUser);
    }

    //update user and verify if the user exists by id
    public UserResponse updateUser(UpdateUserRequest updateUserRequest) {
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
    public void login(LoginRequest loginRequest) {
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
        userRepository.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    //get all users
    public List<UserResponse> getAllUsers() {
        List<UserModel> users = userRepository.findAll();
        return users.stream().map(UserResponse::new).collect(Collectors.toList());
    }

    //me
    public UserResponse getMe() {
        UUID id = extractUserIdFromCookie();
        UserModel user = userRepository.me(id);
        return new UserResponse(user);
    }

    private UUID extractUserIdFromCookie() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_REQUEST_CONTEXT", "Missing request context");
        }

        HttpServletRequest request = attributes.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_ID_COOKIE", "Missing id cookie");
        }

        for (Cookie cookie : cookies) {
            if ("id".equals(cookie.getName())) {
                String cookieValue = cookie.getValue();
                if (cookieValue == null || cookieValue.isBlank()) {
                    throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_ID_COOKIE", "Missing id cookie");
                }
                try {
                    return UUID.fromString(cookieValue);
                } catch (IllegalArgumentException e) {
                    throw new BusinessException(HttpStatus.BAD_REQUEST, "INVALID_ID_COOKIE", "Invalid id cookie");
                }
            }
        }

        throw new BusinessException(HttpStatus.UNAUTHORIZED, "MISSING_ID_COOKIE", "Missing id cookie");
    }

    //delete user by id from params without dto and verify if the user exists by id return void
    public void deleteUser(UUID id) {
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
