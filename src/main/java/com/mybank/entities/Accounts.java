package com.mybank.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.mybank.dtos.utilities.AccountName;
import com.mybank.dtos.utilities.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountNumber;


    @Enumerated(EnumType.STRING)
    private AccountName accountName; //(e.g SALARY, JOINT, BILLS, TRAVEL, PRIMARY, EMERGENCY_FUND)

    @Enumerated(EnumType.STRING)
    private AccountType accountType; //(e.g., SAVING, CURRENT, SYSTEM, PREMIUM)

    //    System generated date
    @Column(name="account_creation_date")
    private LocalDateTime accountCreationDate;

    private double balanceAmount;
//Only declaration for setter and getter
    private  String ifscCode;

    private String bankName;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference   // to avoid recursion in REST responses.
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();
}
