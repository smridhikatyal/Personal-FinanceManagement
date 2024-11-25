package com.example.transaction.service;
// src/main/java/com/example/demo/service/UserService.java



import com.example.transaction.entity.Transaction;
import com.example.transaction.entity.User;
import com.example.transaction.repository.TransactionRepository;
import com.example.transaction.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;



@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public String login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.filter(value -> value.getPassword().equals(password)).isPresent() ? "Login Done" : "Invalid Credentials";
    }

    public boolean withdraw(Long userId, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user has reached the maximum transaction limit
        if (user.getTransactionCount() >= 4) {
            throw new RuntimeException("Transaction limit exceeded for the day.");
        }

        // Check if the user has sufficient balance
        if (user.getBalance() >= amount) {
            // Deduct amount from balance and increment transaction count
            user.setBalance(user.getBalance() - amount);
            user.setTransactionCount(user.getTransactionCount() + 1);

            // Save updated user details
            userRepository.save(user);

            // Log the transaction
            transactionRepository.save(new Transaction(user, -amount, LocalDateTime.now()));

            return true;
        }

        throw new RuntimeException("Insufficient funds for withdrawal.");
    }

    public List<Transaction> getTransactionHistory(Long userId) {
        // Ensure user exists before fetching transactions
        return userRepository.findById(userId)
                .map(user -> transactionRepository.findByUser(user)) // Assuming this returns List<Transaction>
                .orElse(Collections.emptyList()); // Return an empty list if the user is not found
    }

    // src/main/java/com/example/demo/service/UserService.java
    public String register(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            return "Email already in use";
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setBalance(1000.0); // Set default balance
        user.setTransactionCount(0);
        userRepository.save(user);
        return "Registration successful";
    }
    public boolean updateUser(Long userId, String email, String password) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (email != null) {
                user.setEmail(email);
            }
            if (password != null) {
                user.setPassword(password);
            }
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Method to delete user by userId
    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }



    public void transferToEmergencyFund(Long userId, Double transferAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Ensure the user has sufficient funds (optional)
        if (user.getBalance() < transferAmount) {
            throw new RuntimeException("Insufficient balance for emergency fund transfer");
        }

        // Deduct the amount from the user's balance and add it to the emergency fund
        user.setBalance(user.getBalance() - transferAmount);
        user.setEmergencyFund(user.getEmergencyFund() + transferAmount);  // Assuming this field exists

        // Save the updated user information
        userRepository.save(user);
    }






    public List<User> getAllUsersWithTransactions() {

        // fetches all users and their transactions due to the relationship setup in User entity
        List<User> users = userRepository.findAllUsersWithTransactions();
        // Initialize transactions for each user to avoid lazy loading issues
        users.forEach(user -> user.getTransactions().size());
        return users;

    }

    public void deposit(Long userId, Double amount) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setBalance(user.getBalance() + amount);
        user.setTransactionCount(user.getTransactionCount() + 1);
        userRepository.save(user);
        transactionRepository.save(new Transaction(user, amount, LocalDateTime.now()));
    }

    public Double getBalance(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getBalance();
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public boolean transferFunds(Long fromUserId, Long toUserId, Double amount) {
        User fromUser = userRepository.findById(fromUserId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User toUser = userRepository.findById(toUserId).orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (fromUser.getBalance() >= amount) {
            fromUser.setBalance(fromUser.getBalance() - amount);
            toUser.setBalance(toUser.getBalance() + amount);
            transactionRepository.save(new Transaction(fromUser, -amount, LocalDateTime.now()));
            transactionRepository.save(new Transaction(toUser, amount, LocalDateTime.now()));
            userRepository.save(fromUser);
            userRepository.save(toUser);
            return true;
        }
        return false;
    }

}


