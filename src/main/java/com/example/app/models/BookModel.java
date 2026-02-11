package com.example.app.models;

import java.util.UUID;

public class BookModel {
    private UUID id;
    private String title;
    private String description;
    private AuthorModel author;
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

    public BookModel(String title, String description, AuthorModel author) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.author = author;
        this.publishedAt = new java.util.Date().toString();
        this.updatedAt = new java.util.Date().toString();
        this.isActive = true;
        this.isPublished = false;
        this.isDeleted = false;
        this.isFeatured = false;
        this.isTrending = false;
        this.isNew = false;
        this.isPopular = false;
        this.isBestSeller = false;
    }
    // getters
    public UUID getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public AuthorModel getAuthor() {
        return author;
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
    // setters
    public void setTitle(String title) {
        this.title = title;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setAuthor(AuthorModel author) {
        this.author = author;
    }
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }
    public void setUpdatedAt(String updatedAt) {
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
