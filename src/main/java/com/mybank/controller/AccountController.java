package com.mybank.controller;

import com.mybank.dtos.AccountDto;
import com.mybank.dtos.AccountDto;
import com.mybank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/openNewAccount")
    public ResponseEntity<AccountDto> openNewAccount(@Valid @RequestBody AccountDto accountDto) {
        AccountDto accountDto1 = accountService.openAnAccount(accountDto);
        return new ResponseEntity<>(accountDto1, HttpStatus.CREATED);
    }

    @PutMapping("updateAccount/{accountNumber}")
    public ResponseEntity<AccountDto> updateAccount(@Valid @RequestBody AccountDto AccountDto, @PathVariable("accountNumber") long accountNumber) {
        AccountDto updatedAccount = accountService.updateAccount(AccountDto, accountNumber);
        System.out.println("Updated Account values: " + updatedAccount);
        return ResponseEntity.ok(updatedAccount);
    }

    @PatchMapping("linkAccountNumber/{accountNumber}")
    public ResponseEntity<AccountDto> linkAccountNumber(@Valid @RequestBody AccountDto AccountDto, @PathVariable("accountNumber") long accountNumber) {
        AccountDto updatedAccount = accountService.linkAccountWithUser(AccountDto.getUserId(), accountNumber);
        System.out.println("Updated Account values: " + updatedAccount);
        return ResponseEntity.ok(updatedAccount);
    }
    @GetMapping("/getAccountDetails/{accountNumber}")
    public ResponseEntity<AccountDto> getSingleAccountDetails(@PathVariable("accountNumber") long accountid) {
        AccountDto AccountDto = accountService.getSingleAccount(accountid);
        return new ResponseEntity<>(AccountDto, HttpStatus.FOUND);
    }

    @GetMapping("/getAllAccountsDetails")
    public ResponseEntity<List<AccountDto>> getAllAccountDetails() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.FOUND);
    }

    @DeleteMapping("/deleteAccount/{accountNumber}")
    public ResponseEntity<String> deleteAccountById(@PathVariable("accountNumber") long accountNumber) {
        String responseMessage = accountService.deleteAccount(accountNumber);
        return new ResponseEntity<>(responseMessage, HttpStatus.GONE);
    }

    @GetMapping("/downloadAccountsReport")
    public ResponseEntity<InputStreamResource> downloadAccountExcel() throws Exception {
        List<AccountDto> listOfAccountsDto = accountService.getAllAccounts();
        ResponseEntity<InputStreamResource> message = accountService.getReport(listOfAccountsDto);// Fetch accounts from DB

        return message;
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<AccountDto>> getAccountsByUser(@PathVariable Long userId) {
        List<AccountDto> accounts = accountService.getAccountsByUserId(userId);
        return ResponseEntity.ok(accounts);
    }
}
