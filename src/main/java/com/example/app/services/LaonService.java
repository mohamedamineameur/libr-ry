package com.example.app.services;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.dtos.laonDTO.CreateLaonRequest;
import com.example.app.dtos.laonDTO.LaonResponse;
import com.example.app.dtos.laonDTO.MarkLaonReturnedRequest;
import com.example.app.exceptions.BusinessException;
import com.example.app.exceptions.NotFoundException;
import com.example.app.models.BookModel;
import com.example.app.models.LaonModel;
import com.example.app.models.UserModel;
import com.example.app.repositories.BookRepository;
import com.example.app.repositories.LaonRepository;
import com.example.app.repositories.UserRepository;
import com.example.app.security.ResourceAuthorizationService;

@Service
@Transactional
public class LaonService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final LaonRepository laonRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ResourceAuthorizationService resourceAuthorizationService;
    private final MessageSource messageSource;

    public LaonService(
        LaonRepository laonRepository,
        UserRepository userRepository,
        BookRepository bookRepository,
        ResourceAuthorizationService resourceAuthorizationService,
        MessageSource messageSource
    ) {
        this.laonRepository = laonRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.resourceAuthorizationService = resourceAuthorizationService;
        this.messageSource = messageSource;
    }

    public LaonResponse createLaon(CreateLaonRequest request) {
        resourceAuthorizationService.assertOwnerOrAdmin(request.getUserId());

        UserModel user = findUserById(request.getUserId());
        BookModel book = findBookById(request.getBookId());

        LaonModel currentLoanForBook = tryFindByBookId(book.getId());
        if (currentLoanForBook != null && !currentLoanForBook.getIsReturned()) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "BOOK_ALREADY_LOANED",
                message("laon.book.already.loaned", "Book is already loaned and not returned yet.")
            );
        }

        LaonModel laon = new LaonModel(user, book);
        return new LaonResponse(laonRepository.save(laon));
    }

    public LaonResponse getById(UUID id) {
        LaonModel laon = findLaonById(id);
        resourceAuthorizationService.assertOwnerOrAdmin(laon.getUser().getId());
        return new LaonResponse(laon);
    }

    public List<LaonResponse> getMine() {
        UUID currentUserId = resourceAuthorizationService.currentUserId();
        return laonRepository.findByUserId(currentUserId).stream().map(LaonResponse::new).collect(Collectors.toList());
    }

    public List<LaonResponse> getMine(int page, int size) {
        UUID currentUserId = resourceAuthorizationService.currentUserId();
        return laonRepository.findByUserId(currentUserId, normalizePage(page), normalizeSize(size))
            .stream()
            .map(LaonResponse::new)
            .collect(Collectors.toList());
    }

    public List<LaonResponse> getAll() {
        return laonRepository.findAll().stream().map(LaonResponse::new).collect(Collectors.toList());
    }

    public List<LaonResponse> getAll(int page, int size) {
        return laonRepository.findAll(normalizePage(page), normalizeSize(size))
            .stream()
            .map(LaonResponse::new)
            .collect(Collectors.toList());
    }

    public LaonResponse markReturned(UUID id, MarkLaonReturnedRequest request) {
        LaonModel current = findLaonById(id);
        resourceAuthorizationService.assertOwnerOrAdmin(current.getUser().getId());

        if (current.getIsReturned() && Boolean.TRUE.equals(request.getIsReturned())) {
            throw new BusinessException(
                HttpStatus.BAD_REQUEST,
                "LOAN_ALREADY_RETURNED",
                message("laon.already.returned", "Loan is already marked as returned.")
            );
        }

        current.setIsReturned(Boolean.TRUE.equals(request.getIsReturned()));
        LaonModel updated = laonRepository.updateReturning(id, current);
        return new LaonResponse(updated);
    }

    private LaonModel findLaonById(UUID id) {
        try {
            return laonRepository.findById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "LOAN_NOT_FOUND",
                message("laon.not.found", "Loan not found.")
            );
        }
    }

    private UserModel findUserById(UUID id) {
        try {
            return userRepository.findById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "USER_NOT_FOUND",
                message("user.not.found", "User not found.")
            );
        }
    }

    private BookModel findBookById(UUID id) {
        try {
            return bookRepository.findById(id);
        } catch (RuntimeException e) {
            throw new NotFoundException(
                "BOOK_NOT_FOUND",
                message("book.not.found", "Book not found.")
            );
        }
    }

    private LaonModel tryFindByBookId(UUID bookId) {
        try {
            return laonRepository.findByBookId(bookId);
        } catch (RuntimeException e) {
            return null;
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
