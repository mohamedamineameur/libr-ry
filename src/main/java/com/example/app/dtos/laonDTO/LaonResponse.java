package com.example.app.dtos.laonDTO;

import java.util.UUID;

import com.example.app.models.LaonModel;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = false)
public class LaonResponse {
    private UUID id;
    private UUID userId;
    private UUID bookId;
    private String loanDate;
    private String returnDate;
    private boolean isReturned;

    public LaonResponse(LaonModel laon) {
        this.id = laon.getId();
        this.userId = laon.getUser() != null ? laon.getUser().getId() : null;
        this.bookId = laon.getBook() != null ? laon.getBook().getId() : null;
        this.loanDate = laon.getLoanDate();
        this.returnDate = laon.getReturnDate();
        this.isReturned = laon.getIsReturned();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getBookId() {
        return bookId;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public boolean getIsReturned() {
        return isReturned;
    }
}
