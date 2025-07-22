package com.mybank.dtos;

import com.mybank.dtos.utilities.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class TransactionDto {

    private String transactionId;
    private TransactionType transactionType;
    private Double amount;
    private String description;
    private LocalDateTime transactionDate;

    private Long accountNumber; // to identify which account it's linked to
}
