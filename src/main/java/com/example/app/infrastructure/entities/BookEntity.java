package com.example.app.infrastructure.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, length = 4000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private AuthorEntity author;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "is_published")
    private boolean isPublished;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "is_featured")
    private boolean isFeatured;

    @Column(name = "is_trending")
    private boolean isTrending;

    @Column(name = "is_new")
    private boolean isNew;

    @Column(name = "is_popular")
    private boolean isPopular;

    @Column(name = "is_best_seller")
    private boolean isBestSeller;

    protected BookEntity() {
    }

    public BookEntity(String title, String description, AuthorEntity author) {
        this.title = title;
        this.description = description;
        this.author = author;
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.isPublished = false;
        this.isDeleted = false;
        this.isFeatured = false;
        this.isTrending = false;
        this.isNew = false;
        this.isPopular = false;
        this.isBestSeller = false;
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

    public AuthorEntity getAuthor() {
        return author;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public LocalDateTime getUpdatedAt() {
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setIsPublished(boolean isPublished) {
        this.isPublished = isPublished;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setIsFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public void setIsTrending(boolean isTrending) {
        this.isTrending = isTrending;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setIsPopular(boolean isPopular) {
        this.isPopular = isPopular;
    }

    public void setIsBestSeller(boolean isBestSeller) {
        this.isBestSeller = isBestSeller;
    }
}
