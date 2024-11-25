package com.example.transaction.repository;

// src/main/java/com/example/demo/repository/UserRepository.java


import com.example.transaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.transactions")
    List<User> findAllUsersWithTransactions();


    @Query("SELECT u FROM User u ORDER BY u.balance DESC")
    List<User> findUsersWithMaxBalance();
}

