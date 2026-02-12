package com.example.app.dtos.securityDTO;

import java.util.UUID;
import com.example.app.models.SecurityModel;
public class securityResponse {
    private UUID id;
    private boolean is2FAEnabled;

    public securityResponse(SecurityModel security) {
        this.id = security.getId();
        this.is2FAEnabled = security.getIs2FAEnabled();
    }

    public UUID getId() {
        return id;
    }
    public boolean getIs2FAEnabled() {
        return is2FAEnabled;
    }
    
}
