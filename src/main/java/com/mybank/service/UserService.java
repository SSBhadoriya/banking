package com.mybank.service;

import com.mybank.dtos.UserDto;
import com.mybank.dtos.UserDtoResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

    UserDtoResponse saveUser(UserDto userDto);

    UserDtoResponse updateUser(UserDto userDto, long userId);

    UserDtoResponse getSingleUser(long userId);

    List<UserDtoResponse> getAllUsers();

    UserDtoResponse searchUser(String keyword);

    String deleteUser(long userId);

// For Dev envt
//    UserDto partialUpdate(long userId,UserDto userDto);
//    For Prod envt
    String partialUpdate(long userId,UserDto userDto);

    ResponseEntity<InputStreamResource> getReport(List<UserDtoResponse> userDtos) throws Exception;
}
