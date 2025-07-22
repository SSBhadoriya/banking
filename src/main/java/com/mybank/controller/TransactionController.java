package com.mybank.controller;

import com.mybank.dtos.TransactionDto;
import com.mybank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/create-transaction")
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionDto transactionDto)
    {
        TransactionDto transactionDto1 = transactionService.createTransaction(transactionDto);
        return new ResponseEntity<>(transactionDto1, HttpStatus.CREATED);
    }

    @GetMapping("/getAllTransactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactions()
    {
        List<TransactionDto> allTransactions = transactionService.getAllTransactions();
        return new ResponseEntity<>(allTransactions,HttpStatus.FOUND);
    }

    @GetMapping("/getTransactionsByAccountNumber/{accountNumber}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByAccountNumber(@PathVariable("accountNumber") Long accountNumber)
    {
        List<TransactionDto> transactionDtoList = transactionService.getTransactionsByAccount(accountNumber);
        return new ResponseEntity<>(transactionDtoList,HttpStatus.FOUND);
    }

    @GetMapping("/getTransactionsById/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionsById(@PathVariable("transactionId") String transactionId)
    {
        TransactionDto transactionDto = transactionService.getTransactionById(transactionId);
        return new ResponseEntity<>(transactionDto,HttpStatus.FOUND);
    }

    @DeleteMapping("/deleteTransaction/{transactionId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable String transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.ok("Transaction deleted successfully with ID: " + transactionId);
    }

//    Generating statement for CUSTOMER
    @GetMapping("/accounts/{accountNumber}/statement")
    public ResponseEntity<?> generateReport(@PathVariable long accountNumber, @RequestParam long userId) throws Exception
    {
        return transactionService.generateStatement(accountNumber, userId);
    }
//    Generating statement for ADMIN/MANAGER
    @GetMapping("/statement")
    public ResponseEntity<?> generateMonthlyStatement(@RequestParam Long userId) throws Exception
    {
        ResponseEntity<InputStreamResource> response= transactionService.generateMonthlyStatement(userId);
        return response;
    }
}
