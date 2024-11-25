package com.example.transaction.controller;

// src/main/java/com/example/demo/controller/UserController.java


import com.example.transaction.entity.Transaction;
import com.example.transaction.entity.User;
import com.example.transaction.repository.TransactionRepository;
import com.example.transaction.repository.UserRepository;
import com.example.transaction.service.LoanService;
import com.example.transaction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        return userService.login(email, password);
    }


    @GetMapping("/transactions/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable Long userId) {
        List<Transaction> transactions = userService.getTransactionHistory(userId);

        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if no transactions found
        }
        return ResponseEntity.ok(transactions); // Return 200 with the list of transactions
    }

    // src/main/java/com/example/demo/controller/UserController.java
    @PostMapping("/register")
    public String register(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        return userService.register(email, password);
    }
    // New: Update user details
    @PutMapping("/user/{userId}")
    public ResponseEntity<String> updateUser(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");

        boolean isUpdated = userService.updateUser(userId, email, password);
        if (isUpdated) {
            return ResponseEntity.ok("User updated successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // New: Delete a user
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        boolean isDeleted = userService.deleteUser(userId);
        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/users/transactions")
    public ResponseEntity<List<User>> getAllUsersWithTransactions() {
        List<User> users = userRepository.findAllUsersWithTransactions();
        return ResponseEntity.ok(users);
    }
    @PostMapping("/fund/rule")
    public ResponseEntity<String> setFundRule(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Double percentage = Double.valueOf(request.get("percentage").toString());

        if (percentage <= 0 || percentage > 100) {
            return ResponseEntity.badRequest().body("Percentage must be between 1 and 100.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmergencyFundPercentage(percentage);  // Assuming a field that stores the percentage
        userRepository.save(user);

        return ResponseEntity.ok("Emergency fund transfer rule set to " + percentage + "%");
    }
    @PostMapping("/{userId}/deposit")
    public ResponseEntity<String> deposit(@PathVariable Long userId, @RequestBody Map<String, Double> request) {
        // Retrieve the deposit amount from the request
        Double amount = request.get("amount");

        // Perform the deposit for the user (existing deposit functionality)
        userService.deposit(userId, amount);

        // Retrieve the user object to check emergency fund percentage
        Optional<User> userOptional = userRepository.findById(userId);

        User user;
        // If user is not found, throw an exception
        if (userOptional.isPresent()) {
           user = userOptional.get();
        } else {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        // Get the emergency fund percentage from the user settings
        Double emergencyFundPercentage = user.getEmergencyFundPercentage();
        Double emergencyFundTransferAmount = 0.0;

        // If emergency fund percentage is set, calculate the transfer amount
        if (emergencyFundPercentage != null && emergencyFundPercentage > 0) {
            emergencyFundTransferAmount = (amount * emergencyFundPercentage) / 100;

            // Transfer the amount to the emergency fund
            userService.transferToEmergencyFund(userId, emergencyFundTransferAmount);  // Assuming this method exists

            // Create a transaction record for the emergency fund transfer (optional)
            transactionRepository.save(new Transaction(user, -emergencyFundTransferAmount, LocalDateTime.now()));
        }

        // Return the response with both deposit and emergency fund transfer details
        return ResponseEntity.ok("Deposited " + amount + " and transferred " + emergencyFundTransferAmount + " to emergency fund.");
    }

    @PostMapping("/{userId}/withdraw")
    public ResponseEntity<String> withdraw(@PathVariable Long userId, @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");
        boolean success = userService.withdraw(userId, amount);
        if (success) {
            return ResponseEntity.ok("Withdrawal successful. Updated balance: " + userService.getBalance(userId));
        } else {
            return ResponseEntity.badRequest().body("Insufficient funds for withdrawal.");
        }
    }


    @PostMapping("/loan/request")
    public ResponseEntity<String> requestLoan(@RequestBody Map<String, Object> request) {
        Long borrowerId = Long.valueOf(request.get("borrowerId").toString());
        Long lenderId = Long.valueOf(request.get("lenderId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());

        String response = loanService.requestLoan(borrowerId, lenderId, amount);
        return ResponseEntity.ok(response);
    }

    // Endpoint to approve a loan
    @PostMapping("/loan/approve/{loanId}")
    public ResponseEntity<String> approveLoan(@PathVariable Long loanId) {
        String response = loanService.approveLoan(loanId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/loan/status/{loanId}")
    public ResponseEntity<String> getLoanStatus(@PathVariable Long loanId) {
        String status = loanService.getLoanStatus(loanId);
        return ResponseEntity.ok("Loan Status: " + status);
    }


    @GetMapping("/{userId}/balance")
    public ResponseEntity<Double> getBalance(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getBalance(userId));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    @PostMapping("/{fromUserId}/transfer/{toUserId}")
    public ResponseEntity<String> transferFunds(
            @PathVariable Long fromUserId,
            @PathVariable Long toUserId,
            @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");
        boolean success = userService.transferFunds(fromUserId, toUserId, amount);
        if (success) {
            return ResponseEntity.ok("Transfer successful.");
        } else {
            return ResponseEntity.badRequest().body("Transfer failed due to insufficient funds or other constraints.");
        }
    }



    @GetMapping("/max-balance")
    public ResponseEntity<List<User>> getUsersWithMaxBalance() {
        List<User> usersWithMaxBalance = userRepository.findUsersWithMaxBalance();
        if (usersWithMaxBalance != null && !usersWithMaxBalance.isEmpty()) {
            return ResponseEntity.ok(usersWithMaxBalance);
        } else {
            return ResponseEntity.notFound().build();  // Return 404 if no users are found
        }
    }
    



}

