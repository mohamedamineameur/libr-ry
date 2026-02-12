package com.example.app.infrastructure.persistences;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.infrastructure.entities.AuthorEntity;
import com.example.app.infrastructure.entities.BookEntity;
import com.example.app.mappers.BookMapper;
import com.example.app.models.BookModel;
import com.example.app.repositories.BookRepository;

@Repository
@Transactional
@SuppressWarnings("null")
public class BookRepositoryImpl implements BookRepository {

    private final BookRepositoryJpa bookRepositoryJpa;
    private final AuthorRepositoryJpa authorRepositoryJpa;

    public BookRepositoryImpl(BookRepositoryJpa bookRepositoryJpa, AuthorRepositoryJpa authorRepositoryJpa) {
        this.bookRepositoryJpa = bookRepositoryJpa;
        this.authorRepositoryJpa = authorRepositoryJpa;
    }

    @Override
    public BookModel findById(UUID id) {
        return BookMapper.toDomain(
            bookRepositoryJpa.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new RuntimeException("Book not found"))
        );
    }

    @Override
    public BookModel save(BookModel book) {
        UUID authorId = book.getAuthor().getId();
        AuthorEntity authorEntity = authorRepositoryJpa.findByIdAndIsDeletedFalse(authorId)
            .orElseThrow(() -> new RuntimeException("Author not found"));
        BookEntity entity = BookMapper.toEntity(book, authorEntity);
        return BookMapper.toDomain(bookRepositoryJpa.save(entity));
    }

    @Override
    public BookModel update(UUID id, BookModel book) {
        BookEntity existing = bookRepositoryJpa.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new RuntimeException("Book not found"));
        UUID authorId = book.getAuthor().getId();
        AuthorEntity authorEntity = authorRepositoryJpa.findByIdAndIsDeletedFalse(authorId)
            .orElseThrow(() -> new RuntimeException("Author not found"));
        BookMapper.applyToEntity(book, existing, authorEntity);
        return BookMapper.toDomain(bookRepositoryJpa.save(existing));
    }

    @Override
    public List<BookModel> findAll() {
        return bookRepositoryJpa.findAllByIsDeletedFalse().stream().map(BookMapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<BookModel> findAll(int page, int size) {
        return bookRepositoryJpa.findAllByIsDeletedFalse(PageRequest.of(page, size))
            .stream()
            .map(BookMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        BookEntity existing = bookRepositoryJpa.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        existing.setIsDeleted(true);
        bookRepositoryJpa.save(existing);
    }
}
