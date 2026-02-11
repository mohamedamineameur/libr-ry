package com.example.app.dtos.userDTO;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class SetActiveRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    public UUID getUserId() {
        return userId;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
