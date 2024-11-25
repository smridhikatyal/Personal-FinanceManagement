package com.example.transaction.service;

import com.example.transaction.entity.Loan;
import com.example.transaction.entity.User;
import com.example.transaction.repository.LoanRepository;
import com.example.transaction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LoanService {


    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;




    public String requestLoan(Long borrowerId, Long lenderId, Double amount) {
        User borrower = userRepository.findById(borrowerId)
                .orElseThrow(() -> new RuntimeException("Borrower not found"));
        User lender = userRepository.findById(lenderId)
                .orElseThrow(() -> new RuntimeException("Lender not found"));

        if (lender.getBalance() < amount) {
            return "Lender does not have sufficient balance";
        }

        // Create the loan request
        Loan loan = new Loan();
        loan.setBorrowerId(borrowerId);
        loan.setLenderId(lenderId);
        loan.setAmount(amount);
        loan.setStatus("Pending");

        loanRepository.save(loan);

        // Transfer the funds
        lender.setBalance(lender.getBalance() - amount);
        borrower.setBalance(borrower.getBalance() + amount);

        userRepository.save(lender);
        userRepository.save(borrower);

        return "Loan request created successfully and pending approval";
    }

    // Approve loan
    public String approveLoan(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus("Approved");
        loanRepository.save(loan);

        return "Loan approved";
    }
    public String getLoanStatus(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        return loan.getStatus();
    }
}


