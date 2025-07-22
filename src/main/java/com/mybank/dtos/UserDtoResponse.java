package com.mybank.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class UserDtoResponse {

    private long userId;
    private String userName;
    private String contactNo;
    private String email;
    private String role;
    private Date registrationDate;
    private Date passwordExpiryDate;

}
