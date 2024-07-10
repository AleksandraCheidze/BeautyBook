package com.example.end.controller.api;

import com.example.end.dto.*;
import com.example.end.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tags(value = {
        @Tag(name = "Users", description = "Operations related to users")
})
@RequestMapping("/api/users")
public interface UserApi {

    @Operation(summary = "Get user by ID. Available to all users", description = "Retrieve a user by their ID.Available to all users.")
    @GetMapping("/{id}")
    UserDetailsDto getById(@Parameter(description = "ID of the user to be obtained. Cannot be empty.", required = true)
                    @PathVariable("id")  Long id);

    @Operation(summary = "Register a new user.Available to all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "User was registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorsDto.class))),
            @ApiResponse(responseCode = "409",
                    description = "User with such email already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponseDto.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    UserDto register(@RequestBody @Valid NewUserDto newUserDto);

    @Operation(
            summary = "Update or add user details.Available to all authorized masters." ,
            description = " Available to all authorized masters.Updates or adds master details.  ",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NewUserDetailsDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "User details updated successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserDetailsDto.class))),
                    @ApiResponse(responseCode = "404",
                            description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = StandardResponseDto.class)))
            })
    @PreAuthorize("hasAuthority('MASTER')")
    @PutMapping("/{userId}/details")
    UserDetailsDto updateUserDetails(
            @Parameter(description = "ID of the user to be updated. Cannot be empty.",
                    required = true) @PathVariable ("userId") Long userId,
            @Parameter(description = "User details to be updated or added.",
                    required = true) @RequestBody @Valid NewUserDetailsDto userDetailsDto);


    @Operation(summary = "Find users by category ID.Available to all users.", description = "Available to all users. Retrieve users associated with a specific category.")
    @GetMapping("/by-category/{categoryId}")
    List<UserDetailsDto> findUsersByCategoryId(@Parameter(description = "ID of the category to filter users by.", required = true)
                                                @PathVariable ("categoryId")  Long categoryId);


    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get all users. Available to ADMIN", description = "Available to ADMIN")
    @GetMapping()
    List<UserDetailsDto> getAllUsers();

    @Operation(summary = "Get all usersMasters.Available to all users", description = "Available to all users")
    @GetMapping("/masters")
    List<UserDetailsDto> getAllMasters();

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete user by ID. Available to ADMIN", description = "Available to ADMIN")
    @DeleteMapping("/{id}")
    void deleteById(@Parameter(description = "ID of the user to be deleted. Cannot be empty.", required = true)
                                    @PathVariable ("id")  Long id);

    @Operation(summary = "Send message to admin", description = "Send message to admin with user details.")
    @PostMapping("/message-admin")
    void sendMessageToAdmin(@RequestBody @Valid AdminMessageRequest messageRequest);
}
