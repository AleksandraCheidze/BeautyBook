package com.example.end.controller;

import com.example.end.controller.api.UserApi;
import com.example.end.dto.*;
import com.example.end.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @CacheEvict(value = {"allMasters", "usersByCategory"}, allEntries = true)
    public UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto) {
        return userService.updateUserDetails(userId, userDetailsDto);
    }

    @Override
    public ResponseEntity<List<UserDetailsDto>> findUsersByCategoryId(Long categoryId) {
        List<UserDetailsDto> users = userService.findUsersByCategoryId(categoryId);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(15, TimeUnit.MINUTES))
                .header("X-Cache-Hint", "public-max-age=900")
                .body(users);
    }

    @Override
    public ResponseEntity<String> confirmMasterByEmail(String email) {
        userService.confirmMasterByEmail(email);
        return ResponseEntity.ok("Master confirmed successfully.");
    }

    @Override
    public ResponseEntity<List<UserDetailsDto>> getAllMasters(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "20") int size) {
        List<UserDetailsDto> masters = userService.getAllMasters(page, size);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(30, TimeUnit.MINUTES))
                .body(masters);
    }

    @Override
    public List<UserDetailsDto> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        return userService.getAllUsers(page, size);
    }

    @Override
    @CacheEvict(value = {"allMasters", "usersByCategory"}, allEntries = true)
    public ResponseEntity<String> deleteById(Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User with ID " + id + " was successfully deleted.");
    }
}

