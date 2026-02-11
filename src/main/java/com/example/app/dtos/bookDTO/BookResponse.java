package com.example.app.dtos.bookDTO;

import java.util.UUID;

import com.example.app.models.AuthorModel;
import com.example.app.models.BookModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class BookResponse {
    private UUID id;
    private String title;
    private String description;
    private UUID authorId;
    private String authorFirstName;
    private String authorLastName;
    private String publishedAt;
    private String updatedAt;
    private boolean isActive;
    private boolean isPublished;
    private boolean isDeleted;
    private boolean isFeatured;
    private boolean isTrending;
    private boolean isNew;
    private boolean isPopular;
    private boolean isBestSeller;

    public BookResponse(BookModel book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.description = book.getDescription();
        AuthorModel author = book.getAuthor();
        this.authorId = author != null ? author.getId() : null;
        this.authorFirstName = author != null ? author.getFirstName() : null;
        this.authorLastName = author != null ? author.getLastName() : null;
        this.publishedAt = book.getPublishedAt();
        this.updatedAt = book.getUpdatedAt();
        this.isActive = book.getIsActive();
        this.isPublished = book.getIsPublished();
        this.isDeleted = book.getIsDeleted();
        this.isFeatured = book.getIsFeatured();
        this.isTrending = book.getIsTrending();
        this.isNew = book.getIsNew();
        this.isPopular = book.getIsPopular();
        this.isBestSeller = book.getIsBestSeller();
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public UUID getAuthorId() {
        return authorId;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public boolean getIsPublished() {
        return isPublished;
    }

    public boolean getIsDeleted() {
        return isDeleted;
    }

    public boolean getIsFeatured() {
        return isFeatured;
    }

    public boolean getIsTrending() {
        return isTrending;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public boolean getIsPopular() {
        return isPopular;
    }

    public boolean getIsBestSeller() {
        return isBestSeller;
    }
}
