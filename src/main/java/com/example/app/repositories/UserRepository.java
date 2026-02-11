package com.example.app.repositories;

import com.example.app.models.UserModel;
import java.util.UUID;
import java.util.List;

public interface UserRepository {
    UserModel findById(UUID id);
    UserModel findByEmail(String email);
    UserModel save(UserModel user);
    List<UserModel> findAll();
    boolean isAdmin(UUID id);
    boolean isActive(UUID id);
    void delete(UUID id);
}
