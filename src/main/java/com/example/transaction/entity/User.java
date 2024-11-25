package com.example.transaction.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
//import javax.validation.constraints.NotNull;
//import javax.persistence.*;





@Entity
@Table(name = "user")//for h2 database
//@Table(name ="user") for sql database user is reserved word
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email" ,unique = true, nullable = false)
    private String email;

    @NotNull
    private String password;

    private Double balance = 1000.0; // Default balance

    private int transactionCount = 0;

    private double EmergencyFundPercentage = 0.0;

    private double EmergencyFund =0.0;

    public double getEmergencyFundPercentage() {
        return EmergencyFundPercentage;
    }

    public void setEmergencyFundPercentage(double emergencyFundPercentage) {
        EmergencyFundPercentage = emergencyFundPercentage;
    }


    public double getEmergencyFund() {
        return EmergencyFund;
    }

    public void setEmergencyFund(double emergencyFund) {
        EmergencyFund = emergencyFund;
    }



    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore // Avoid infinite recursion in JSON serialization
    private List<Transaction> transactions;

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }




}

//@Entity: Marks this class as a database entity.
//@Id and @GeneratedValue: Specifies id as the primary key with auto-increment.
//@Column: Defines the email as unique.
//balance: Stores the user’s current balance.
//transactionCount: Tracks the number of transactions by this user.

