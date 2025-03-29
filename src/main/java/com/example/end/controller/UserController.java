package com.example.end.controller;

import com.example.end.controller.api.UserApi;
import com.example.end.dto.*;
import com.example.end.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API endpoints for user management")
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get User by ID (Public)", description = "Get a specific user by their ID. Access: All users")
    public UserDetailsDto getById(Long id) {
        return userService.getById(id);
    }

    @Override
    @PostMapping("/register")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Register New User (Public)", description = "Register a new user in the system. Access: All users")
    public UserDto register(NewUserDto newUserDto) {
        return userService.register(newUserDto);
    }

    @Override
    @PutMapping("/{userId}/details")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "Update User Details (MASTER)", description = "Update or add user details. Access: Authorized masters only")
    @SecurityRequirement(name = "bearerAuth")
    public UserDetailsDto updateUserDetails(Long userId, NewUserDetailsDto userDetailsDto) {
        return userService.updateUserDetails(userId, userDetailsDto);
    }

    @Override
    @GetMapping("/by-category/{categoryId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get Users by Category (Public)", description = "Get all users associated with a specific category. Access: All users")
    public List<UserDetailsDto> findUsersByCategoryId(Long categoryId) {
        return userService.findUsersByCategoryId(categoryId);
    }

    @Override
    @PostMapping("/confirm-master-by-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Confirm Master by Email (ADMIN)", description = "Confirm a master user by their email. Access: ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> confirmMasterByEmail(String email) {
        userService.confirmMasterByEmail(email);
        return ResponseEntity.ok("Master confirmed successfully.");
    }

    @Override
    @GetMapping("/masters")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get All Masters (Public)", description = "Get all master users in the system. Access: All users")
    public List<UserDetailsDto> getAllMasters() {
        return userService.getAllMasters();
    }

    @Override
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get All Users (ADMIN)", description = "Get all users in the system. Access: ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    public List<UserDetailsDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete User (ADMIN)", description = "Delete a user from the system. Access: ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<String> deleteById(Long id) {
        userService.deleteById(id);
        return ResponseEntity.ok("User with ID " + id + " was successfully deleted.");
    }
}

