package com.mybank.controller;

import com.mybank.dtos.UserDto;
import com.mybank.dtos.UserDtoResponse;
import com.mybank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/save")
    public ResponseEntity<UserDtoResponse> createUser(@Valid @RequestBody UserDto userDto) {

        UserDtoResponse savedUser = userService.saveUser(userDto);
        System.out.println("User values: "+userDto);
        return ResponseEntity.ok(savedUser);
    }
    @PutMapping("updateUser/{userId}")
    public ResponseEntity<UserDtoResponse> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") long userId) {
        UserDtoResponse updatedUser = userService.updateUser(userDto,userId);
        System.out.println("Updated User values: "+updatedUser);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/getUser/{userId}")
    public ResponseEntity<UserDtoResponse> getSingleUserDetails(@PathVariable("userId") long userid)
    {
        UserDtoResponse userDto = userService.getSingleUser(userid);
        return new ResponseEntity<>(userDto, HttpStatus.FOUND);
    }

    @GetMapping("/getAllUsersDetails")
    public ResponseEntity<List<UserDtoResponse>> getAllUserDetails()
    {
        List<UserDtoResponse> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.FOUND);
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable("userId") long userId)
    {
        String responseMessage = userService.deleteUser(userId);
        return new ResponseEntity<>(responseMessage,HttpStatus.GONE);
    }

//  localhost:9091/users/password-change-request/1
    @PatchMapping("/password-change-request/{userId}")
    //    public ResponseEntity<UserDto> passwordEncryption(@PathVariable("userId") long userId, @RequestBody UserDto userDto)
    public ResponseEntity<String> passwordEncryption(@PathVariable("userId") long userId, @RequestBody UserDto userDto)
    {
//        For Dev envt:
//        UserDto newUserDto = userService.partialUpdate(userId,userDto);
//      for prod envt
        String message = userService.partialUpdate(userId,userDto);
        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }




    @GetMapping("/downloadUsersReport")
    public ResponseEntity<InputStreamResource> downloadUserExcel() throws Exception {
        List<UserDtoResponse> userDtos = userService.getAllUsers();
        ResponseEntity<InputStreamResource> message = userService.getReport(userDtos);// Fetch users from DB

       return message;
    }


}
