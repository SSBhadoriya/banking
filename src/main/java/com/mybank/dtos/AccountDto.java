package com.mybank.dtos;

import com.mybank.dtos.utilities.AccountName;
import com.mybank.dtos.utilities.AccountType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {
    private long accountNumber;

    private AccountName accountName; //(e.g SALARY, JOINT, BILLS, TRAVEL, PRIMARY, EMERGENCY_FUND)

    private AccountType accountType; //(e.g., SAVING, CURRENT, SYSTEM, PREMIUM)
    //    System generated date
    private LocalDateTime accountCreationDate;

    private double balanceAmount;

    //Only declaration for setter and getter
    private  String ifscCode;

    private String bankName;

//    private UserDto user;  // Optional, only if you want to show user info in account API

    private Long userId;       // To link with user
    private String userName;   // For display purpose

}
