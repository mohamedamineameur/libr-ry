package com.example.app.dtos.userDTO;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class SetAdminRequest {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
    @NotNull(message = "Admin status is required")
    private Boolean isAdmin;
    public UUID getUserId() {
        return userId;
    }
    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}