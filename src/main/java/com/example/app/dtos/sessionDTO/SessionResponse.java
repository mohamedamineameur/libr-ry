package com.example.app.dtos.sessionDTO;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.app.models.SessionModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class SessionResponse {
    private UUID id;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private String ipAddress;
    private String userAgent;
    private String browser;
    private String os;
    private String country;
    private String city;
    private String region;
    private String latitude;
    private String longitude;
    private String timezone;

    public SessionResponse(SessionModel session) {
        this.id = session.getId();
        this.userId = session.getUser() != null ? session.getUser().getId() : null;
        this.createdAt = session.getCreatedAt();
        this.expiresAt = session.getExpiresAt();
        this.isActive = session.getIsActive();
        this.ipAddress = session.getIpAddress();
        this.userAgent = session.getUserAgent();
        this.browser = session.getBrowser();
        this.os = session.getOs();
        this.country = session.getCountry();
        this.city = session.getCity();
        this.region = session.getRegion();
        this.latitude = session.getLatitude();
        this.longitude = session.getLongitude();
        this.timezone = session.getTimezone();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getBrowser() {
        return browser;
    }

    public String getOs() {
        return os;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getTimezone() {
        return timezone;
    }
}
