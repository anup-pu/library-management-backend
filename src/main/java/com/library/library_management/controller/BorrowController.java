package com.library.library_management.controller;

import com.library.library_management.dto.BorrowDTO;
import com.library.library_management.model.Book;
import com.library.library_management.model.Borrow;
import com.library.library_management.model.User;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.BorrowRepository;
import com.library.library_management.repository.UserRepository;
import com.library.library_management.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/borrow")
@CrossOrigin(origins = "http://localhost:3000")
public class BorrowController {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private Long getUserIdFromToken(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.map(User::getId).orElse(null);
    }

    @PostMapping("/{bookId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> borrowBook(@PathVariable Long bookId, @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) return ResponseEntity.badRequest().body("Invalid or missing user token");

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Book> bookOpt = bookRepository.findById(bookId);

        if (userOpt.isEmpty() || bookOpt.isEmpty()) return ResponseEntity.badRequest().body("Invalid book or user");

        Book book = bookOpt.get();
        if (!book.isAvailable()) return ResponseEntity.badRequest().body("Book is already borrowed");

        // Borrow logic
        book.setAvailable(false);
        bookRepository.save(book);

        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setUser(userOpt.get());
        borrow.setBorrowDate(LocalDate.now());

        Borrow saved = borrowRepository.save(borrow);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/return/{bookId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> returnBook(@PathVariable Long bookId, @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) return ResponseEntity.badRequest().body("Invalid token");

        Optional<Borrow> borrowOpt = borrowRepository.findByUserId(userId).stream()
            .filter(b -> b.getBook().getId().equals(bookId) && b.getReturnDate() == null)
            .findFirst();

        if (borrowOpt.isEmpty()) return ResponseEntity.badRequest().body("No active borrow found");

        Borrow borrow = borrowOpt.get();
        borrow.setReturnDate(LocalDate.now());
        borrowRepository.save(borrow);

        Book book = borrow.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        return ResponseEntity.ok("Book returned successfully");
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<Borrow>> myBorrowedBooks(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        if (userId == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(borrowRepository.findByUserId(userId));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BorrowDTO>> allBorrowRecords() {
        List<Borrow> borrows = borrowRepository.findAll();
        List<BorrowDTO> dtoList = borrows.stream().map(BorrowDTO::new).toList();
        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/delete/{borrowId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteBorrowRecord(@PathVariable Long borrowId) {
        Optional<Borrow> borrowOpt = borrowRepository.findById(borrowId);
        if (borrowOpt.isEmpty()) return ResponseEntity.notFound().build();

        Borrow borrow = borrowOpt.get();
        Book book = borrow.getBook();

        // Make the book available again if it was borrowed
        if (borrow.getReturnDate() == null) {
            book.setAvailable(true);
            bookRepository.save(book);
        }

        borrowRepository.deleteById(borrowId);
        return ResponseEntity.ok("Borrow record deleted successfully");
    }
    @DeleteMapping("/my/delete/{borrowId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> deleteMyBorrow(@PathVariable Long borrowId, @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        Optional<Borrow> borrowOpt = borrowRepository.findById(borrowId);

    if (borrowOpt.isEmpty() || !borrowOpt.get().getUser().getId().equals(userId)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized or not found");
    }

    borrowRepository.deleteById(borrowId);
    return ResponseEntity.ok("Borrow record deleted");
}

}
