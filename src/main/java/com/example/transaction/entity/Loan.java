package com.example.transaction.entity;


import jakarta.persistence.*;






@Entity
@Table(name = "loan")
    public class Loan {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long loanId;

        private Long borrowerId;
        private Long lenderId;
        private Double amount;
        private String status; // Pending, Approved, Rejected, etc.

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getLoanId() {
            return loanId;
        }

        public void setLoanId(Long loanId) {
            this.loanId = loanId;
        }

        public Long getBorrowerId() {
            return borrowerId;
        }

        public void setBorrowerId(Long borrowerId) {
            this.borrowerId = borrowerId;
        }

        public Long getLenderId() {
            return lenderId;
        }

        public void setLenderId(Long lenderId) {
            this.lenderId = lenderId;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }




    }



