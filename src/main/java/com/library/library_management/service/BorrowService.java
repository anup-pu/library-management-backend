package com.library.library_management.service;


import com.library.library_management.model.Borrow;
import com.library.library_management.model.Book;
import com.library.library_management.model.User;
import com.library.library_management.repository.BorrowRepository;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Borrow borrowBook(Long bookId, String email) {
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!book.isAvailable()) {
            throw new RuntimeException("Book is not available");
        }

        book.setAvailable(false);
        bookRepository.save(book);

        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setUser(user);
        borrow.setBorrowDate(LocalDate.now());
        return borrowRepository.save(borrow);
    }

    public List<Borrow> getBorrowedBooksByUser(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return borrowRepository.findByUser(user);
    }

    public void returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId)
            .orElseThrow(() -> new RuntimeException("Borrow record not found"));

        Book book = borrow.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        borrow.setReturnDate(LocalDate.now());
        borrowRepository.save(borrow);
    }
}
