package com.mybank.entities;

import com.mybank.dtos.utilities.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Transaction {

    @Id
    private String transactionId;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // e.g. CREDIT, DEBIT
    private Double amount;

    private String description;

    private LocalDateTime transactionDate;

    // relationships One a/c has many transactions
    @ManyToOne
    @JoinColumn(name = "account_number")
    private Accounts account;
}

