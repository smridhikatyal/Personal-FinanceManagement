package com.example.transaction.repository;

import com.example.transaction.entity.Loan;
import com.example.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findById(Long id);

}
