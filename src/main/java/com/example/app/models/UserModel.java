package com.example.app.models;

import java.util.UUID;


public class UserModel {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private String createdAt;
    private String updatedAt;
    private boolean isActive;
    private boolean isAdmin;

    public UserModel(String name, String email, String password) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.password = password;
        this.createdAt = new java.util.Date().toString();
        this.updatedAt = new java.util.Date().toString();
        this.isActive = true;
        this.isAdmin = false;
    }
    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    public boolean getIsActive() {
        return isActive;
    }
    public boolean getIsAdmin() {
        return isAdmin;
    }
}
