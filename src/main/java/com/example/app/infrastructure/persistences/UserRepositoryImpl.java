package com.example.app.infrastructure.persistences;

import com.example.app.repositories.UserRepository;
import com.example.app.models.UserModel;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.mappers.UserMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.List;
import java.util.Optional;
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
   
}
