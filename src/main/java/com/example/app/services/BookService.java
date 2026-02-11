package com.example.app.services;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dtos.bookDTO.BookResponse;
import com.example.app.dtos.bookDTO.CreateBookRequest;
import com.example.app.dtos.bookDTO.UpdateBookRequest;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;
import com.example.app.repositories.AuthorRepository;
import com.example.app.repositories.BookRepository;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final MessageSource messageSource;

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, MessageSource messageSource) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.messageSource = messageSource;
    }

    public BookResponse createBook(CreateBookRequest request) {
        AuthorModel author = findAuthorDomainById(request.getAuthorId());
        BookModel book = new BookModel(request.getTitle(), request.getDescription(), author);
        applyFlagsFromCreateRequest(book, request);
        return new BookResponse(bookRepository.save(book));
    }

    public BookResponse updateBook(UUID id, UpdateBookRequest request) {
        BookModel current = findBookDomainById(id);
        AuthorModel author = request.getAuthorId() != null ? findAuthorDomainById(request.getAuthorId()) : current.getAuthor();

        BookModel merged = new BookModel(
            request.getTitle() != null ? request.getTitle() : current.getTitle(),
            request.getDescription() != null ? request.getDescription() : current.getDescription(),
            author
        );
        merged.setId(current.getId());
        merged.setPublishedAt(current.getPublishedAt());
        merged.setIsActive(request.getIsActive() != null ? request.getIsActive() : current.getIsActive());
        merged.setIsPublished(request.getIsPublished() != null ? request.getIsPublished() : current.getIsPublished());
        merged.setIsDeleted(request.getIsDeleted() != null ? request.getIsDeleted() : current.getIsDeleted());
        merged.setIsFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : current.getIsFeatured());
        merged.setIsTrending(request.getIsTrending() != null ? request.getIsTrending() : current.getIsTrending());
        merged.setIsNew(request.getIsNew() != null ? request.getIsNew() : current.getIsNew());
        merged.setIsPopular(request.getIsPopular() != null ? request.getIsPopular() : current.getIsPopular());
        merged.setIsBestSeller(request.getIsBestSeller() != null ? request.getIsBestSeller() : current.getIsBestSeller());

        return new BookResponse(bookRepository.update(id, merged));
    }

    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream().map(BookResponse::new).collect(Collectors.toList());
    }

    public BookResponse getBookById(UUID id) {
        return new BookResponse(findBookDomainById(id));
    }

    public void deleteBook(UUID id) {
        findBookDomainById(id);
        bookRepository.delete(id);
    }

    private void applyFlagsFromCreateRequest(BookModel book, CreateBookRequest request) {
        if (request.getIsActive() != null) {
            book.setIsActive(request.getIsActive());
        }
        if (request.getIsPublished() != null) {
            book.setIsPublished(request.getIsPublished());
        }
        if (request.getIsDeleted() != null) {
            book.setIsDeleted(request.getIsDeleted());
        }
        if (request.getIsFeatured() != null) {
            book.setIsFeatured(request.getIsFeatured());
        }
        if (request.getIsTrending() != null) {
            book.setIsTrending(request.getIsTrending());
        }
        if (request.getIsNew() != null) {
            book.setIsNew(request.getIsNew());
        }
        if (request.getIsPopular() != null) {
            book.setIsPopular(request.getIsPopular());
        }
        if (request.getIsBestSeller() != null) {
            book.setIsBestSeller(request.getIsBestSeller());
        }
    }

    private BookModel findBookDomainById(UUID id) {
        try {
            return bookRepository.findById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "BOOK_NOT_FOUND",
                message("book.not.found", "Book not found.")
            );
        }
    }

    private AuthorModel findAuthorDomainById(UUID id) {
        try {
            return authorRepository.findById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "AUTHOR_NOT_FOUND",
                message("author.not.found", "Author not found.")
            );
        }
    }

    @SuppressWarnings("null")
    private @NonNull String message(@NonNull String key, @NonNull String defaultMessage) {
        return Objects.requireNonNullElse(
            messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale()),
            defaultMessage
        );
    }
}
