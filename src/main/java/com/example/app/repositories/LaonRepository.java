package com.example.app.repositories;

import java.util.List;
import java.util.UUID;

import com.example.app.models.LaonModel;

public interface LaonRepository {
    LaonModel findById(UUID id);
    List<LaonModel> findByUserId(UUID userId);
    List<LaonModel> findByUserId(UUID userId, int page, int size);
    LaonModel findByBookId(UUID bookId);
    LaonModel save(LaonModel loan);
    LaonModel updateReturning(UUID id, LaonModel loan);
    List<LaonModel> findAll();
    List<LaonModel> findAll(int page, int size);
    
}
