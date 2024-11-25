package com.example.transaction.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "transaction")
public class Transaction {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double amount;
    private LocalDateTime timestamp;


    public Transaction(User user, Double amount, LocalDateTime timestamp) {
        this.user = user;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    // Default constructor (required by JPA)
    public Transaction() {}
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
