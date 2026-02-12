package com.example.app.models;


import java.util.UUID;

public class LaonModel {
    private UUID id;
    private UserModel user;
    private BookModel book;
    private String loanDate;
    private String returnDate;
    private boolean isReturned;

    public LaonModel(UserModel user, BookModel book) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.book = book;
        this.loanDate = new java.util.Date().toString();
        // return date is 30 days from loan date
        this.returnDate = new java.util.Date(new java.util.Date().getTime() + 30 * 24 * 60 * 60 * 1000).toString();
        this.isReturned = false;
    }   
    // getters
    public UUID getId() {
        return id;
    }
    public UserModel getUser() {
        return user;
    }
    public BookModel getBook() {
        return book;
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
    // setters
   
    public void setId(UUID id) {
        this.id = id;
    }
    public void setUser(UserModel user) {
        this.user = user;
    }
    public void setBook(BookModel book) {
        this.book = book;
    }
    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
    public void setIsReturned(boolean isReturned) {
        this.isReturned = isReturned;
    }
}
