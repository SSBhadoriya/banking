package com.mybank.service.impl;

import com.mybank.dtos.AccountDto;
import com.mybank.entities.Accounts;
import com.mybank.entities.User;
import com.mybank.exception.ResourceNotFoundException;
import com.mybank.repos.AccountRepo;
import com.mybank.repos.UserRepo;
import com.mybank.service.AccountService;
import com.mybank.dtos.utilities.BankConstant;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Override
    public AccountDto openAnAccount(AccountDto accountDto) {

        Accounts accountDetails = modelMapper.map(accountDto, Accounts.class);
        accountDetails.setIfscCode(BankConstant.ifscCode);
        accountDetails.setBankName(BankConstant.bankName);
        accountDetails.setAccountCreationDate(LocalDateTime.now());
        if(accountDto.getUserId()!=null)
        {User user = userRepo.findById(accountDto.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Invalid User Id: " + accountDto.getUserId()));
        accountDetails.setUser(user);
        }
        Accounts savedAccount = accountRepo.save(accountDetails);
        return modelMapper.map(savedAccount, AccountDto.class);
    }

    @Override
    public AccountDto updateAccount(AccountDto accountDto, long accountNumber) {
        Accounts accounts = accountRepo.findById(accountNumber) .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        if(accounts != null)
        {
            accounts.setAccountName(accountDto.getAccountName());
            accounts.setAccountType(accountDto.getAccountType());
            accounts.setAccountCreationDate(LocalDateTime.now());  //updating date as well
            accounts.setBalanceAmount(accountDto.getBalanceAmount());
            accounts.setBankName(accountDto.getBankName());
            accounts.setIfscCode(accountDto.getIfscCode());
            if(accountDto.getUserId()!=null)
            {User user = userRepo.findById(accountDto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid User Id: " + accountDto.getUserId()));
                accounts.setUser(user);
            }
            accounts = accountRepo.save(accounts);
        }
            return modelMapper.map(accounts, AccountDto.class);
    }

    @Override
    public AccountDto linkAccountWithUser(Long userId, long accountNumber) {
        Accounts accounts = accountRepo.findById(accountNumber) .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Invalid user id!!!"));
        accounts.setUser(user);
        Accounts saved = accountRepo.save(accounts);
        return modelMapper.map(saved, AccountDto.class);
    }

    @Override
    public AccountDto getSingleAccount(long accountNumber) {
        Accounts account = accountRepo.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));

        return modelMapper.map(account, AccountDto.class);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Accounts> accounts = accountRepo.findAll();
        List<AccountDto> dtos = accounts.stream()
                .map(account -> modelMapper.map(account, AccountDto.class))
                .collect(Collectors.toList());

        return dtos;
    }

    @Override
    public AccountDto searchAccount(String keyword) {
        return null;
    }

    @Override
    public String deleteAccount(long accountNumber) {
        Accounts account = accountRepo.findById(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));

        accountRepo.delete(account);
        return "Account deleted successfully!!!";
    }

    @Override
    public ResponseEntity<InputStreamResource> getReport(List<AccountDto> listOfAccountsDto) throws Exception {
        StringWriter writer = new StringWriter();
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Account No", "Account Name", "Account Type", "Balance", "Bank", "IFSC"));

        for (AccountDto dto : listOfAccountsDto) {
            csvPrinter.printRecord(
                    dto.getAccountNumber(),
                    dto.getAccountName(),
                    dto.getAccountType(),
                    dto.getBalanceAmount(),
                    dto.getBankName(),
                    dto.getIfscCode()
            );
        }

        csvPrinter.flush();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes(StandardCharsets.UTF_8));
        InputStreamResource resource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=account_report.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }

    @Override
    public List<AccountDto> getAccountsByUserId(Long userId) {
        List<Accounts> accounts = accountRepo.findByUserUserId(userId);
        return accounts.stream()
                .map(account -> {
                    AccountDto dto = modelMapper.map(account, AccountDto.class);
                    if (account.getUser() != null) {
                        dto.setUserId(account.getUser().getUserId());
                        dto.setUserName(account.getUser().getUserName());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
