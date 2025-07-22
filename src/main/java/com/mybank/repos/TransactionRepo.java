package com.mybank.repos;

import com.mybank.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountAccountNumber(Long accountNumber);

    List<Transaction> findByAccountAccountNumberAndTransactionDateBetween(Long accountNumber, LocalDateTime startOfMonth, LocalDateTime endOfMonth);

    List<Transaction> findByTransactionDateBetween(LocalDateTime startOfMonth, LocalDateTime endOfMonth);
}
