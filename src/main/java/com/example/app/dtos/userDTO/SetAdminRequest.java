package com.example.app.dtos.userDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class SetAdminRequest {
    
    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format")
    private UUID userId;
    @NotBlank(message = "Admin status is required")
    private boolean isAdmin;
    public UUID getUserId() {
        return userId;
    }
    public boolean getIsAdmin() {
        return isAdmin;
    }
}