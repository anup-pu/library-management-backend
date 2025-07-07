package com.library.library_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate borrowDate = LocalDate.now();
    private LocalDate returnDate;

    @ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "book_id", nullable = false)
@JsonIgnoreProperties({"borrowedRecords"}) // 👈 prevents infinite nesting
private Book book;

@ManyToOne(fetch = FetchType.EAGER)
@JoinColumn(name = "user_id", nullable = false)
@JsonIgnoreProperties({"borrowedBooks"}) // 👈 if User has List<Borrow>
private User user;

}
