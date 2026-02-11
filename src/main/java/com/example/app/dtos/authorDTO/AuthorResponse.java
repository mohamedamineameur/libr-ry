package com.example.app.dtos.authorDTO;

import java.util.UUID;

import com.example.app.models.AuthorModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class AuthorResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String biography;

    public AuthorResponse(AuthorModel author) {
        this.id = author.getId();
        this.firstName = author.getFirstName();
        this.lastName = author.getLastName();
        this.email = author.getEmail();
        this.biography = author.getBiography();
    }

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
}
