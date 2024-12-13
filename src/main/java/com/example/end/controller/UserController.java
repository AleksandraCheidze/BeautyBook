package com.example.end.controller;

import com.example.end.controller.api.UserApi;
import com.example.end.dto.*;
import com.example.end.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public UserDetailsDto getById(Long id) {
        return userService.getById(id);
    }


    @Override
    public UserDto register(NewUserDto newUserDto) {
        return userService.register(newUserDto);
    }

    @Override
    public UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto) {
        return userService.updateUserDetails(userId, userDetailsDto);
    }

    @Override
    public List<UserDetailsDto> findUsersByCategoryId(Long categoryId) {
        return userService.findUsersByCategoryId(categoryId);
    }
    @Override
    public ResponseEntity<String> confirmMasterByEmail(String email) {
        userService.confirmMasterByEmail(email);
        return ResponseEntity.ok("Master confirmed successfully.");
    }

    @Override
    public List<UserDetailsDto> getAllMasters() {
        return userService.getAllMasters() ;
    }

    @Override
    public List<UserDetailsDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    public void deleteById(Long id) {
       userService.deleteById(id);
    }

    }

