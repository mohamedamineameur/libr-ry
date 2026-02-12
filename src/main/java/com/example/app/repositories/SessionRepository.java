package com.example.app.repositories;

import java.util.List;
import java.util.UUID;

import com.example.app.models.SessionModel;

public interface SessionRepository {
    SessionModel save(SessionModel session);

    SessionModel findById(UUID sessionId);

    List<SessionModel> findByUserId(UUID userId);

    List<SessionModel> findAll();

    SessionModel setActive(UUID sessionId, boolean isActive);
}
