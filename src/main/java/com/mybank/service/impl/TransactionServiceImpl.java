package com.mybank.service.impl;

import com.mybank.dtos.TransactionDto;
import com.mybank.dtos.utilities.Role;
import com.mybank.dtos.utilities.TransactionType;
import com.mybank.entities.Accounts;
import com.mybank.entities.Transaction;
import com.mybank.entities.User;
import com.mybank.exception.ResourceNotFoundException;
import com.mybank.repos.AccountRepo;
import com.mybank.repos.TransactionRepo;
import com.mybank.repos.UserRepo;
import com.mybank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

//This is Spring’s recommended modern approach using constructor injection with Lombok.
//When you annotate your class with @RequiredArgsConstructor, Lombok automatically generates a constructor for all final fields:
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final ModelMapper modelMapper;
    private final UserRepo userRepo;

    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        Transaction transaction = modelMapper.map(transactionDto, Transaction.class);
        transaction.setTransactionDate(LocalDateTime.now());

        Accounts account = accountRepo.findById(transactionDto.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + transactionDto.getAccountNumber()));

        transaction.setAccount(account);
        transaction.setTransactionId(UUID.randomUUID().toString()); // generating string ID

        //Optional code
        // ✅ AUTOMATE BALANCE UPDATE
        TransactionType type = transactionDto.getTransactionType(); // Assuming you're using enum
        double amount = transactionDto.getAmount();
        // Update account balance
        if (transactionDto.getTransactionType()==(TransactionType.CREDIT)) {
            account.setBalanceAmount(account.getBalanceAmount() + transactionDto.getAmount());
        }  else if (transactionDto.getTransactionType() == TransactionType.DEBIT) {
            if (account.getBalanceAmount() < transactionDto.getAmount()) {
                throw new IllegalArgumentException("Insufficient balance.");
            }
            account.setBalanceAmount(account.getBalanceAmount() - transactionDto.getAmount());
        }

        Transaction saved = transactionRepo.save(transaction);
        return modelMapper.map(saved, TransactionDto.class);
    }

    @Override
    public List<TransactionDto> getAllTransactions() {
        List<Transaction> transactionList = transactionRepo.findAll();
        return transactionList.stream().map(transaction -> modelMapper.map(transaction, TransactionDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getTransactionsByAccount(Long accountNumber) {
        List<Transaction> transactionList = transactionRepo.findByAccountAccountNumber(accountNumber);
        return transactionList.stream().map(transaction -> modelMapper.map(transaction, TransactionDto.class)).collect(Collectors.toList());
    }

    @Override
    public TransactionDto getTransactionById(String transactionId) {
        Transaction transaction = transactionRepo.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException("Invalid Transaction ID: " + transactionId));
        return modelMapper.map(transaction,TransactionDto.class);
    }

    @Override
    public void deleteTransaction(String transactionId) {
        Transaction tx = transactionRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionId));

        transactionRepo.delete(tx);
    }

    @Override
    public ResponseEntity<InputStreamResource> generateStatement(Long accountNumber, Long userId) throws Exception {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Transaction> transactions;

        if (user.getRole() == Role.CUSTOMER) {
            transactions = transactionRepo.findByAccountAccountNumber(accountNumber);
        } /*else if (user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER) {
            LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
            transactions = transactionRepo.findByAccountAccountNumberAndTransactionDateBetween(
                    accountNumber, startOfMonth, endOfMonth);}*/
         else {
            throw new IllegalAccessException("Unauthorized user role: " + user.getRole());
        }

        // Excel Export using Apache POI
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Account Statement");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction ID");
        header.createCell(1).setCellValue("Type");
        header.createCell(2).setCellValue("Amount");
        header.createCell(3).setCellValue("Description");
        header.createCell(4).setCellValue("Date & Time");
        header.createCell(5).setCellValue("Account Number");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(t.getTransactionId());
            row.createCell(1).setCellValue(t.getTransactionType().name());
            row.createCell(2).setCellValue(t.getAmount());
            row.createCell(3).setCellValue(t.getDescription());
            row.createCell(4).setCellValue(t.getTransactionDate().format(formatter));

            // Show account number only for first row to avoid repetition
            if (i == 0) {
                row.createCell(5).setCellValue(t.getAccount().getAccountNumber());
            } else {
                row.createCell(5).setCellValue(""); // leave blank
            }
        }

        for (int col = 0; col <= 5; col++) {
            sheet.autoSizeColumn(col);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=account_statement.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

//    Fetching all users data



    @Override
    public ResponseEntity<InputStreamResource> generateMonthlyStatement(Long userId) throws Exception {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (user.getRole() != Role.ADMIN && user.getRole() != Role.MANAGER) {
            throw new IllegalAccessException("Only ADMIN or MANAGER can access this report.");
        }

        // Define monthly range
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);

        List<Transaction> transactions = transactionRepo.findByTransactionDateBetween(startOfMonth, endOfMonth);

        // Excel logic
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Monthly Transactions");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Transaction ID");
        header.createCell(1).setCellValue("Type");
        header.createCell(2).setCellValue("Amount");
        header.createCell(3).setCellValue("Description");
        header.createCell(4).setCellValue("Date & Time");
        header.createCell(5).setCellValue("Account No");
        header.createCell(6).setCellValue("User Name");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(t.getTransactionId());
            row.createCell(1).setCellValue(t.getTransactionType().name());
            row.createCell(2).setCellValue(t.getAmount());
            row.createCell(3).setCellValue(t.getDescription());
            row.createCell(4).setCellValue(t.getTransactionDate().format(formatter));
            row.createCell(5).setCellValue(t.getAccount().getAccountNumber());

            if (t.getAccount().getUser() != null) {
                row.createCell(6).setCellValue(t.getAccount().getUser().getUserName());
            } else {
                row.createCell(6).setCellValue("N/A");
            }
        }

        for (int i = 0; i <= 6; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=monthly_statement.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(file);
    }

}
