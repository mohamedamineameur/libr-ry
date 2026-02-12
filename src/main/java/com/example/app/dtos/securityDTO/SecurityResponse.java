package com.example.app.dtos.securityDTO;

import java.util.UUID;

import com.example.app.models.SecurityModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class SecurityResponse {
    private UUID id;
    private boolean is2FAEnabled;
    private boolean isMailVerified;

    public SecurityResponse(SecurityModel security) {
        this.id = security.getId();
        this.is2FAEnabled = security.getIs2FAEnabled();
        this.isMailVerified = security.getIsMailVerified();
    }

    public UUID getId() {
        return id;
    }

    public boolean getIs2FAEnabled() {
        return is2FAEnabled;
    }

    public boolean getIsMailVerified() {
        return isMailVerified;
    }
}
