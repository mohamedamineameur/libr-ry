package com.example.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.app.dtos.userDTO.ChangePasswordRequest;
import com.example.app.dtos.userDTO.CreateUserRequest;
import com.example.app.dtos.userDTO.SetActiveRequest;
import com.example.app.dtos.userDTO.SetAdminRequest;
import com.example.app.dtos.userDTO.UpdateUserRequest;
import com.example.app.dtos.userDTO.UserResponse;
import com.example.app.exceptions.BusinessException;
import com.example.app.exceptions.GlobalExceptionHandler;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.UserModel;
import com.example.app.services.UserService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

        mockMvc = MockMvcBuilders
            .standaloneSetup(new UserController(userService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
            .build();
    }

    @Test
    @DisplayName("Check that create user returns 201 and response body when payload is valid")
    void createUserShouldReturnCreatedAndBody() throws Exception {
        UserModel model = new UserModel("John", "john@example.com", "hashed");
        UserResponse response = new UserResponse(model);
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "John",
                      "email": "john@example.com",
                      "password": "AmAm198905@",
                      "confirmPassword": "AmAm198905@"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Check that create user returns 400 when payload validation fails")
    void createUserShouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "",
                      "email": "bad-email",
                      "password": "123",
                      "confirmPassword": "321"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Check that create user returns 400 when an unknown JSON field is sent")
    void createUserShouldReturnBadRequestWhenUnknownFieldIsProvided() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "name": "John",
                      "email": "john@example.com",
                      "password": "AmAm198905@",
                      "confirmPassword": "AmAm198905@",
                      "unknown": "boom"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("UNKNOWN_FIELD"));
    }

    @Test
    @DisplayName("Check that update user returns 200 for a valid request")
    void updateUserShouldReturnOk() throws Exception {
        UserModel model = new UserModel("John", "john@example.com", "hashed");
        UserResponse response = new UserResponse(model);
        when(userService.updateUser(any(UpdateUserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "id": "11111111-1111-1111-1111-111111111111",
                      "name": "John",
                      "email": "john@example.com"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    @DisplayName("Check that change password returns 200 for a valid request")
    void changePasswordShouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        UserModel model = new UserModel("John", "john@example.com", "hashed");
        UserResponse response = new UserResponse(model);
        when(userService.changePassword(eq(id), any(ChangePasswordRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/users/password/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "oldPassword": "OldPass1@",
                      "newPassword": "NewPass1@",
                      "confirmPassword": "NewPass1@"
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that set active returns 200 for a valid request")
    void setActiveShouldReturnOk() throws Exception {
        UserModel model = new UserModel("John", "john@example.com", "hashed");
        UserResponse response = new UserResponse(model);
        when(userService.setActive(any(SetActiveRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/users/active")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "userId": "11111111-1111-1111-1111-111111111111",
                      "isActive": true
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that set admin returns 200 for a valid request")
    void setAdminShouldReturnOk() throws Exception {
        UserModel model = new UserModel("John", "john@example.com", "hashed");
        UserResponse response = new UserResponse(model);
        when(userService.setAdmin(any(SetAdminRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/users/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "userId": "11111111-1111-1111-1111-111111111111",
                      "isAdmin": true
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that login sets token cookie when credentials are valid")
    void loginShouldSetTokenCookie() throws Exception {
        when(userService.login(org.mockito.ArgumentMatchers.any())).thenReturn("token123");

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "a@a.a",
                      "password": "AmAm198905@"
                    }
                    """))
            .andExpect(status().isNoContent())
            .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("token=token123")));
    }

    @Test
    @DisplayName("Check that login returns 400 when payload validation fails")
    void loginShouldReturnBadRequestWhenValidationFails() throws Exception {
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "",
                      "password": ""
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Check that login returns business error when service rejects credentials")
    void loginShouldReturnBusinessErrorWhenServiceThrows() throws Exception {
        when(userService.login(any())).thenThrow(new BusinessException(
            org.springframework.http.HttpStatus.BAD_REQUEST,
            "PASSWORD_INCORRECT",
            "Current password is incorrect."
        ));

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "a@a.a",
                      "password": "bad"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("PASSWORD_INCORRECT"));
    }

    @Test
    @DisplayName("Check that get me returns 200 when service succeeds")
    void getMeShouldReturnOk() throws Exception {
        UserModel model = new UserModel("John", "john@example.com", "hashed");
        UserResponse response = new UserResponse(model);
        when(userService.getMe()).thenReturn(response);

        mockMvc.perform(get("/users/me"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that get all users returns 200 when service succeeds")
    void getAllUsersShouldReturnOk() throws Exception {
        UserModel model = new UserModel("John", "john@example.com", "hashed");
        UserResponse response = new UserResponse(model);
        when(userService.getAllUsers()).thenReturn(List.of(response));

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Check that delete user returns 204 when service succeeds")
    void deleteUserShouldReturnNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/users/{id}", id))
            .andExpect(status().isNoContent());

        verify(userService).deleteUser(id);
    }

    @Test
    @DisplayName("Check that delete user returns 404 when service throws not found")
    void deleteUserShouldReturnNotFoundWhenServiceThrows() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new NotFoundException("USER_NOT_FOUND", "User not found.")).when(userService).deleteUser(id);

        mockMvc.perform(delete("/users/{id}", id))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }
}
