package com.library.library_management.repository;

import com.library.library_management.model.Borrow;
import com.library.library_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByUser(User user);
    List<Borrow> findByUserId(Long userId);
}

