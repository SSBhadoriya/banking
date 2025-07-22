package com.mybank.service;

import com.mybank.dtos.TransactionDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionService {

    TransactionDto createTransaction(TransactionDto transactionDto);

    List<TransactionDto> getAllTransactions();

    List<TransactionDto> getTransactionsByAccount(Long accountNumber);

    TransactionDto getTransactionById(String transactionId);

    void deleteTransaction(String transactionId);

    ResponseEntity<InputStreamResource> generateStatement(Long accountNumber, Long userId) throws Exception;

    ResponseEntity<InputStreamResource> generateMonthlyStatement(Long userId) throws Exception;
}