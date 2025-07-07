package com.library.library_management.dto;

import com.library.library_management.model.Borrow;

import java.time.LocalDate;

public class BorrowDTO {
    private Long borrowId;            // ✅ Needed for deletion
    private String username;
    private String title;
    private String author;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String email;             // ✅ Added for user email
    

    public BorrowDTO(Borrow b) {
        this.borrowId = b.getId();                   // ✅ Added
        this.username = b.getUser().getUsername();
        this.title = b.getBook().getTitle();
        this.author = b.getBook().getAuthor();
        this.borrowDate = b.getBorrowDate();
        this.returnDate = b.getReturnDate();
        this.email = b.getUser().getEmail(); 
    }

    // Getters
    public Long getBorrowId() { return borrowId; }
    public String getUsername() { return username; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public String getEmail() {return email;}
}
