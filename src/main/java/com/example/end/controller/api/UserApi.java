package com.example.end.controller.api;

import com.example.end.dto.*;
import com.example.end.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tags(value = {
                @Tag(name = "Users", description = "Operations related to users")
})
@RequestMapping("/api/users")
public interface UserApi {

        @Operation(summary = "Get User by ID (Public)", description = "Get a specific user by their ID. Access: All users")
        @GetMapping("/{id}")
        UserDetailsDto getById(
                        @Parameter(description = "ID of the user to be obtained. Cannot be empty.", required = true) @PathVariable("id") Long id);

        @Operation(summary = "Register New User (Public)", description = "Register a new user in the system. Access: All users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "User was registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))),
                        @ApiResponse(responseCode = "409", description = "User with such email already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @ResponseStatus(HttpStatus.CREATED)
        @PostMapping("/register")
        UserDto register(@RequestBody @Valid NewUserDto newUserDto);

        @Operation(summary = "Update User Details (MASTER)", description = "Update or add user details. Access: Authorized masters only")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "User details updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsDto.class))),
                        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @SecurityRequirement(name = "bearerAuth")
        @PutMapping("/{userId}/details")
        UserDetailsDto updateUserDetails(
                        @Parameter(description = "ID of the user to be updated. Cannot be empty.", required = true) @PathVariable("userId") Long userId,
                        @Parameter(description = "User details to be updated or added.", required = true) @RequestBody @Valid NewUserDetailsDto userDetailsDto);

        @Operation(summary = "Get Users by Category (Public)", description = "Get all users associated with a specific category. Access: All users")
        @GetMapping("/by-category/{categoryId}")
        List<UserDetailsDto> findUsersByCategoryId(
                        @Parameter(description = "ID of the category to filter users by.", required = true) @PathVariable("categoryId") Long categoryId);

        @Operation(summary = "Get All Users (ADMIN)", description = "Get all users in the system. Access: ADMIN only")
        @GetMapping()
        @SecurityRequirement(name = "bearerAuth")
        List<UserDetailsDto> getAllUsers();

        @Operation(summary = "Get All Masters (Public)", description = "Get all master users in the system. Access: All users")
        @GetMapping("/masters")
        List<UserDetailsDto> getAllMasters();

        @Operation(summary = "Delete User (ADMIN)", description = "Delete a user from the system. Access: ADMIN only")
        @DeleteMapping("/{id}")
        @SecurityRequirement(name = "bearerAuth")
        ResponseEntity<String> deleteById(
                        @Parameter(description = "ID of the user to be deleted. Cannot be empty.", required = true) @PathVariable("id") Long id);

        @Operation(summary = "Confirm Master by Email (ADMIN)", description = "Confirm a master user by their email. Access: ADMIN only")
        @PostMapping("/confirm-master-by-email")
        @SecurityRequirement(name = "bearerAuth")
        ResponseEntity<String> confirmMasterByEmail(
                        @Parameter(description = "Email of the master to be confirmed.", required = true) @RequestParam String email);
}
