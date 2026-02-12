package com.example.app.infrastructure.persistences;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.infrastructure.entities.SessionEntity;
import com.example.app.infrastructure.entities.UserEntity;
import com.example.app.mappers.SessionMapper;
import com.example.app.models.SessionModel;
import com.example.app.repositories.SessionRepository;

@Repository
@Transactional
@SuppressWarnings("null")
public class SessionRepositoryImpl implements SessionRepository {

    private final SessionRepositoryJpa sessionRepositoryJpa;
    private final UserRepositoryJpa userRepositoryJpa;

    public SessionRepositoryImpl(SessionRepositoryJpa sessionRepositoryJpa, UserRepositoryJpa userRepositoryJpa) {
        this.sessionRepositoryJpa = sessionRepositoryJpa;
        this.userRepositoryJpa = userRepositoryJpa;
    }

    @Override
    public SessionModel save(SessionModel session) {
        UUID userId = session.getUser().getId();
        UserEntity userEntity = userRepositoryJpa.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        SessionEntity entity = SessionMapper.toEntity(session, userEntity);
        return SessionMapper.toDomain(sessionRepositoryJpa.save(entity));
    }

    @Override
    public SessionModel findById(UUID sessionId) {
        return SessionMapper.toDomain(
            sessionRepositoryJpa.findById(sessionId).orElseThrow(() -> new RuntimeException("Session not found"))
        );
    }

    @Override
    public List<SessionModel> findByUserId(UUID userId) {
        return sessionRepositoryJpa.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(SessionMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SessionModel> findAll() {
        return sessionRepositoryJpa.findAll()
            .stream()
            .map(SessionMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public SessionModel setActive(UUID sessionId, boolean isActive) {
        SessionEntity session = sessionRepositoryJpa.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        session.setIsActive(isActive);
        return SessionMapper.toDomain(sessionRepositoryJpa.save(session));
    }
}
