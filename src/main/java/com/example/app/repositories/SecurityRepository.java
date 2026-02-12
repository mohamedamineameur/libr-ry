package com.example.app.repositories;

import java.util.UUID;

import com.example.app.models.SecurityModel;

public interface SecurityRepository {
    SecurityModel save(SecurityModel security);

    SecurityModel findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
