package com.example.app.infrastructure.persistences;

import com.example.app.repositories.UserRepository;
import com.example.app.models.UserModel;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.mappers.UserMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;



@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
    private final UserRepositoryJpa userRepositoryJpa;

    public UserRepositoryImpl(UserRepositoryJpa userRepositoryJpa) {
        this.userRepositoryJpa = userRepositoryJpa;
    }

    @Override
    public UserModel findById(UUID id) {
        return UserMapper.toDomain(userRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }
    @Override
    public UserModel findByEmail(String email) {
        return UserMapper.toDomain(userRepositoryJpa.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")));
    }
    @Override
    public boolean existsByEmail(String email) {
        return userRepositoryJpa.findByEmail(email).isPresent();
    }
    @Override
    public UserModel save(UserModel user) {
        return UserMapper.toDomain(userRepositoryJpa.save(UserMapper.toEntity(user)));
    }
    @Override
    public List<UserModel> findAll() {
        return userRepositoryJpa.findAll().stream().map(UserMapper::toDomain).collect(Collectors.toList());
    }
    @Override
    public boolean isAdmin(UUID id) {
        return userRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("User not found")).getIsAdmin();
    }
    @Override
    public boolean isActive(UUID id) {
        return userRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("User not found")).getIsActive();
    }
    @Override
    public void delete(UUID id) {
        userRepositoryJpa.deleteById(id);
    }
    @Override
    public UserModel me(UUID id) {
        UserEntity user = userRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toDomain(user);
    }
    @Override
    public void login(String email, String password) {
        userRepositoryJpa.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }
    @Override
    public UserModel setAdmin(UUID id, boolean isAdmin) {
        UserEntity user = userRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsAdmin(isAdmin);
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.toDomain(userRepositoryJpa.save(user));
    }
    @Override
    public UserModel setActive(UUID id, boolean isActive) {
        UserEntity user = userRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(isActive);
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.toDomain(userRepositoryJpa.save(user));
    }
    @Override
    public UserModel updateUser(UUID id, String name, String email) {
        UserEntity user = userRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(name);
        user.setEmail(email);
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.toDomain(userRepositoryJpa.save(user));
    }
    @Override
    public UserModel changePassword(UUID id, String oldPassword, String newPassword, String confirmPassword) {
        UserEntity user = userRepositoryJpa.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        return UserMapper.toDomain(userRepositoryJpa.save(user));
    }
}
