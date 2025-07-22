package com.mybank.service.impl;

import com.mybank.dtos.UserDto;
import com.mybank.dtos.UserDtoResponse;
import com.mybank.entities.User;
import com.mybank.repos.UserRepo;
import com.mybank.service.UserService;
import com.mybank.service.reports.UserReportService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public UserDtoResponse saveUser(UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRegistrationDate(new Date());
        LocalDateTime expiryDateTime = LocalDateTime.now().plusDays(90);
        Date expiryDate = Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant());
        user.setPasswordExpiryDate(expiryDate);
        User savedUser = userRepo.save(user);
        System.out.println("User Id:"+savedUser.getUserId());


        return modelMapper.map(savedUser, UserDtoResponse.class);
    }

    @Override
    public UserDtoResponse updateUser(UserDto userDto, long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("No User found, Invalid User Id: " + userId));

        user.setUserName(userDto.getUserName());
        user.setEmail(userDto.getEmail());
        user.setContactNo(userDto.getContactNo());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        LocalDateTime expiryDateTime = LocalDateTime.now().plusDays(90);
        Date expiryDate = Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant());
        user.setPasswordExpiryDate(expiryDate);
        user.setRole(userDto.getRole());
        User saved = userRepo.save(user);
        return modelMapper.map(saved,UserDtoResponse.class);
    }

    @Override
    public UserDtoResponse getSingleUser(long userId) {

        User user = userRepo.findById(userId).orElse(new User());
        return modelMapper.map(user,UserDtoResponse.class);
    }

    @Override
    public List<UserDtoResponse> getAllUsers() {

        List<User> userList = userRepo.findAll();
        List<UserDtoResponse> userDtoList = userList.stream().map((user) -> modelMapper.map(user, UserDtoResponse.class)).collect(Collectors.toList());
        return userDtoList;
    }

    @Override
    public String deleteUser(long userId) {
        User user = userRepo.findById(userId).orElseThrow(()->new RuntimeException("Invalid User ID:"+userId));

        String userName = user.getUserName();
        long id = user.getUserId();
//        userRepo.deleteById(userId);
        userRepo.delete(user);
        return "User "+userName+" with ID: "+id+" Deleted Successfully";

    }

    @Override
    public UserDtoResponse searchUser(String keyword) {
        return null;
    }



//    @Override
//    public UserDto partialUpdate(long userId, UserDto userDto) {
//        User user = userRepo.findById(userId).orElseThrow(()->new RuntimeException("Invalid User ID:"+userId));
//       //Cleansing Activity before setting new password
//         user.setPassword(null);
//        System.out.println("Clearing Older Encoded Password in Dev Envt:"+user.getPassword());
//          user.setPassword(passwordEncoder.encode(userDto.getPassword())); //new password
//        System.out.println("Setting new Encoded Password in Dev Envt:"+user.getPassword());
//        User saved = userRepo.save(user);
//        return modelMapper.map(saved,UserDto.class);
//    }

//    For prod environment
@Override
public String partialUpdate(long userId, UserDto userDto) {
    User user = userRepo.findById(userId).orElseThrow(()->new RuntimeException("Invalid User ID:"+userId));
    //Cleansing Activity before setting new password
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    user.setPassword(null);
    System.out.println("Old password cleared...");
    user.setPassword(passwordEncoder.encode(userDto.getPassword())); //new password
    System.out.println("Password changed Successfully...");
    LocalDateTime expiryDateTime = LocalDateTime.now().plusDays(90);
    Date expiryDate = Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant());
    user.setPasswordExpiryDate(expiryDate);
    System.out.println("Current Password will expire on: "+user.getPasswordExpiryDate());
    System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    User saved = userRepo.save(user);
    return "Password changed successfully.\nPlease change this new password on/before "+user.getPasswordExpiryDate();
}

    @Autowired
    private UserReportService userReportService;


    @Override
    public ResponseEntity<InputStreamResource> getReport(List<UserDtoResponse> userDtos) throws Exception{

        List<User> users = userDtos.stream().map((userDto)->modelMapper.map(userDto,User.class)).collect(Collectors.toList()) ;

        ByteArrayInputStream in = userReportService.generateUserExcel(users);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }
}
