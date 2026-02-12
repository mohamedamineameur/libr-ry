package com.example.app.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.app.dtos.userDTO.CreateUserRequest;
import com.example.app.dtos.userDTO.LoginRequest;
import com.example.app.dtos.userDTO.UpdateUserRequest;
import com.example.app.dtos.userDTO.UserResponse;
import com.example.app.exceptions.BusinessException;
import com.example.app.models.SessionModel;
import com.example.app.models.UserModel;
import com.example.app.repositories.UserRepository;
import com.example.app.security.ResourceAuthorizationService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageSource messageSource;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private SessionService sessionService;
    @Mock
    private SecurityService securityService;

    @Mock
    private ResourceAuthorizationService resourceAuthorizationService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
            userRepository,
            messageSource,
            bCryptPasswordEncoder,
            tokenService,
            sessionService,
            securityService,
            resourceAuthorizationService
        );
    }

    @Test
    @DisplayName("Check that creating a user fails when the email already exists")
    void createUserShouldFailWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("Aa1@aaaa");
        request.setConfirmPassword("Aa1@aaaa");
        request.setName("John");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.createUser(request));
        assertEquals("USER_EMAIL_ALREADY_EXISTS", ex.getCode());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    @DisplayName("Check that creating a user succeeds with valid data")
    void createUserShouldSucceed() {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("Aa1@aaaa");
        request.setConfirmPassword("Aa1@aaaa");
        request.setName("John");

        UserModel saved = new UserModel("John", "test@example.com", "hashed");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(bCryptPasswordEncoder.encode("Aa1@aaaa")).thenReturn("hashed");
        when(userRepository.save(any(UserModel.class))).thenReturn(saved);
        when(securityService.createForUser(any(UserModel.class))).thenAnswer(invocation -> new com.example.app.models.SecurityModel(invocation.getArgument(0)));

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    @DisplayName("Check that login returns a token for valid credentials")
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain");

        UserModel user = new UserModel("John", "test@example.com", "hashed");
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("plain", "hashed")).thenReturn(true);
        when(securityService.isMailVerified(userId)).thenReturn(true);
        when(securityService.is2FAEnabled(userId)).thenReturn(false);
        SessionModel session = new SessionModel(user, "127.0.0.1", "ua", "Chrome", "Linux", null, null, null, null, null, null);
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        when(sessionService.createSession(user, "127.0.0.1", "ua")).thenReturn(session);
        when(tokenService.generateToken(sessionId)).thenReturn("token-value");

        String token = userService.login(request, "127.0.0.1", "ua");

        assertEquals("token-value", token);
    }

    @Test
    @DisplayName("Check that login is forbidden when user email is not verified")
    void loginShouldFailWhenEmailIsNotVerified() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("plain");

        UserModel user = new UserModel("John", "test@example.com", "hashed");
        UUID userId = UUID.randomUUID();
        user.setId(userId);
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("plain", "hashed")).thenReturn(true);
        when(securityService.isMailVerified(userId)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(request, "127.0.0.1", "ua"));

        assertEquals("EMAIL_NOT_VERIFIED", ex.getCode());
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatus());
    }

    @Test
    @DisplayName("Check that update fails when user id is missing")
    void updateUserShouldFailWhenIdIsMissing() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("John");

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.updateUser(request));
        assertEquals("MISSING_USER_ID", ex.getCode());
    }

    @Test
    @DisplayName("Check that getMe reads the current user id from authorization service")
    void getMeShouldUseCurrentUserFromAuthorizationService() {
        UUID currentUser = UUID.randomUUID();
        UserModel user = new UserModel("John", "john@example.com", "hashed");
        user.setId(currentUser);
        when(resourceAuthorizationService.currentUserId()).thenReturn(currentUser);
        when(userRepository.me(currentUser)).thenReturn(user);

        UserResponse response = userService.getMe();

        verify(resourceAuthorizationService).currentUserId();
        assertEquals("john@example.com", response.getEmail());
    }
}
