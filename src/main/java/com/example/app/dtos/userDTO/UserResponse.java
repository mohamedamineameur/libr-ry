package com.example.app.dtos.userDTO;

import com.example.app.models.UserModel;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;
    private boolean isActive;
    private boolean isAdmin;

    public UserResponse(UserModel user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.isActive = user.getIsActive();
        this.isAdmin = user.getIsAdmin();
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
    public String getCreatedAt() {
        return createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public boolean getIsActive() {
        return isActive;
    }
    public boolean getIsAdmin() {
        return isAdmin;
    }
}
