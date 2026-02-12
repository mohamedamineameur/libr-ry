package com.example.app.models;

import java.util.UUID;
import java.time.LocalDateTime;

public class SessionModel {
    private UUID id;
    private UserModel user;
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


    public SessionModel(UserModel user, String ipAddress, String userAgent, String browser, String os, String country, String city, String region, String latitude, String longitude, String timezone) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(1);
        this.isActive = true;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.browser = browser;
        this.os = os;
        this.country = country;
        this.city = city;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
    }

    // getters
    public UUID getId() {
        return id;
    }
    public UserModel getUser() {
        return user;
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

    // setters
    public void setUser(UserModel user) {
        this.user = user;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public void setBrowser(String browser) {
        this.browser = browser;
    }
    public void setOs(String os) {
        this.os = os;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
   
  
}
