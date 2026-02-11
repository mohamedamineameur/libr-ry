package com.example.app.repositories;

import java.util.List;
import java.util.UUID;

import com.example.app.models.BookModel;

public interface BookRepository {
    BookModel findById(UUID id);

    BookModel save(BookModel book);

    BookModel update(UUID id, BookModel book);

    List<BookModel> findAll();

    void delete(UUID id);
}
