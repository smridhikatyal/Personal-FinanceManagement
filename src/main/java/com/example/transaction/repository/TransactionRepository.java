// src/main/java/com/example/demo/repository/TransactionRepository.java
package com.example.transaction.repository;

import com.example.transaction.entity.Transaction;
import com.example.transaction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
}

