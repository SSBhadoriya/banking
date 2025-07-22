package com.mybank.service;

import com.mybank.dtos.AccountDto;
import com.mybank.dtos.AccountDto;
import com.mybank.dtos.AccountDto;
import com.mybank.dtos.UserDtoResponse;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {

    AccountDto openAnAccount(AccountDto accountDto);

    AccountDto updateAccount(AccountDto accountDto, long accountNumber);

    AccountDto getSingleAccount(long accountNumber);

    List<AccountDto> getAllAccounts();

    AccountDto searchAccount(String keyword);

    String deleteAccount(long accountNumber);

    ResponseEntity<InputStreamResource> getReport(List<AccountDto> listOfAccountsDto) throws Exception;

    List<AccountDto> getAccountsByUserId(Long userId);

    AccountDto linkAccountWithUser(Long userId, long accountNumber);
}
