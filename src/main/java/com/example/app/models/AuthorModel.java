package com.example.app.models;

import java.util.UUID;

public class AuthorModel {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String biography;
    private boolean isDeleted;

    public AuthorModel(String firstName, String lastName, String email, String biography) {
        this.id = UUID.randomUUID();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.biography = biography;
        this.isDeleted = false;
    }
  
    
    // getters
    public UUID getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getBiography() {
        return biography;
    }
    public boolean getIsDeleted() {
        return isDeleted;
    }
    // setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setBiography(String biography) {
        this.biography = biography;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
  
}
