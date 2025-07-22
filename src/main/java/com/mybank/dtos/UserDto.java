package com.mybank.dtos;

import com.mybank.dtos.utilities.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.util.Date;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Builder
public class UserDto {

    private long userId;

//    @NotNull   //: Ensures a field is not null.
//    @NotEmpty  //: Guarantees that collections or arrays are not empty.
    @NotBlank(message="Please enter a valid user name")    //: Enforces non-nullity and requires at least one non-whitespace character.
    private String userName;

    @NotBlank(message="Please enter a valid phone number")
    private String contactNo;

    @Email
    @NotBlank(message="Please enter a valid email ID")
    private String email;

    @NotBlank(message="Please enter a valid Password")
    private String password;

    @NotBlank(message="Please enter a valid Role")
    private Role role;  //CUSTOMER, ADMIN, MANAGER

//    System generated date
    private Date registrationDate;
//      Will be calculated using service layer during password reset in every 90days cycle
    private Date passwordExpiryDate;



}
