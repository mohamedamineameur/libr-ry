package com.example.app.infrastructure.persistences;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.app.infrastructure.entities.UserEntity;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

public interface UserRepositoryJpa extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findById(UUID id);
    UserEntity save(UserEntity user);
    void deleteById(UUID id);
    List<UserEntity> findAll();
    boolean is_admin(UUID id);
    boolean is_active(UUID id);
    UserEntity changePassword(UUID id, String oldPassword, String newPassword, String confirmPassword);
    UserEntity updateUser(UUID id, String name, String email);
    UserEntity setActive(UUID id, boolean isActive);
    UserEntity setAdmin(UUID id, boolean isAdmin);
    void delete(UUID id);
}
   