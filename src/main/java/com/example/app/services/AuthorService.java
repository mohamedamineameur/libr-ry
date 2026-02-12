package com.example.app.services;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dtos.authorDTO.AuthorResponse;
import com.example.app.dtos.authorDTO.CreateAuthorRequest;
import com.example.app.dtos.authorDTO.UpdateAuthorRequest;
import com.example.app.exceptions.BusinessException;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.AuthorModel;
import com.example.app.repositories.AuthorRepository;

@Service
@Transactional
public class AuthorService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final AuthorRepository authorRepository;
    private final MessageSource messageSource;

    public AuthorService(AuthorRepository authorRepository, MessageSource messageSource) {
        this.authorRepository = authorRepository;
        this.messageSource = messageSource;
    }

    public AuthorResponse createAuthor(CreateAuthorRequest request) {
        if (authorRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "AUTHOR_EMAIL_ALREADY_EXISTS",
                message("author.email.already.exists", "Author email already exists.")
            );
        }

        AuthorModel author = new AuthorModel(
            request.getFirstName(),
            request.getLastName(),
            request.getEmail(),
            request.getBiography()
        );
        return new AuthorResponse(authorRepository.save(author));
    }

    public AuthorResponse updateAuthor(UUID id, UpdateAuthorRequest request) {
        AuthorModel current = findAuthorDomainById(id);
        String email = request.getEmail() != null ? request.getEmail() : current.getEmail();

        if (!current.getEmail().equalsIgnoreCase(email) && authorRepository.existsByEmail(email)) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "AUTHOR_EMAIL_ALREADY_EXISTS",
                message("author.email.already.exists", "Author email already exists.")
            );
        }

        AuthorModel updated = authorRepository.update(
            id,
            request.getFirstName() != null ? request.getFirstName() : current.getFirstName(),
            request.getLastName() != null ? request.getLastName() : current.getLastName(),
            email,
            request.getBiography() != null ? request.getBiography() : current.getBiography()
        );
        return new AuthorResponse(updated);
    }

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream().map(AuthorResponse::new).collect(Collectors.toList());
    }

    public List<AuthorResponse> getAllAuthors(int page, int size) {
        int safePage = normalizePage(page);
        int safeSize = normalizeSize(size);
        return authorRepository.findAll(safePage, safeSize).stream().map(AuthorResponse::new).collect(Collectors.toList());
    }

    public AuthorResponse getAuthorById(UUID id) {
        return new AuthorResponse(findAuthorDomainById(id));
    }

    public void deleteAuthor(UUID id) {
        findAuthorDomainById(id);
        authorRepository.delete(id);
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

    private int normalizePage(int page) {
        return Math.max(page, DEFAULT_PAGE);
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    @SuppressWarnings("null")
    private @NonNull String message(@NonNull String key, @NonNull String defaultMessage) {
        return Objects.requireNonNullElse(
            messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale()),
            defaultMessage
        );
    }
}
