package com.example.app.dtos.userDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class SetActiveRequest {

    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$", message = "Invalid UUID format")
    private UUID userId;
    @NotBlank(message = "Active status is required")
    private boolean isActive;
    public UUID getUserId() {
        return userId;
    }
    public boolean getIsActive() {
        return isActive;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
}
