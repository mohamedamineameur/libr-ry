package com.example.app.repositories;

import com.example.app.models.UserModel;
import java.util.UUID;
import java.util.List;

public interface UserRepository {
    UserModel findById(UUID id);
    UserModel findByEmail(String email);
    boolean existsByEmail(String email);
    UserModel save(UserModel user);
    UserModel changePassword(UUID id, String oldPassword, String newPassword, String confirmPassword);
    UserModel updateUser(UUID id, String name, String email);
    UserModel setActive(UUID id, boolean isActive);
    UserModel setAdmin(UUID id, boolean isAdmin);
    UserModel me(UUID id);
    void login(String email, String password);
    List<UserModel> findAll();
    boolean isAdmin(UUID id);
    boolean isActive(UUID id);
    void delete(UUID id);
}
