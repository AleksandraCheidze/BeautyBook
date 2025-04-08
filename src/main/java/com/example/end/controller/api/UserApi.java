package com.example.end.controller.api;

import com.example.end.dto.*;
import com.example.end.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.persistence.Cacheable;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tags(value = {
        @Tag(name = "Users", description = "Operations related to users")
})
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@ApiResponses(value = {
        @ApiResponse(responseCode = "401",
                description = "User is not authenticated",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = StandardResponseDto.class))),
        @ApiResponse(responseCode = "403",
                description = "Forbidden",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = StandardResponseDto.class)))
})
public interface UserApi {

        @Operation(summary = "Get User by ID (Public)", description = "Get a specific user by their ID. Access: All users")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200",
                        description = "Successful operation",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = UserDetailsDto.class))),
                @ApiResponse(responseCode = "404",
                        description = "User not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @SecurityRequirement(name = "none")
        @GetMapping("/{id}")
        UserDetailsDto getById(
                @Parameter(description = "ID of the user to be obtained. Cannot be empty.", required = true)
                @PathVariable("id") Long id);

        @Operation(summary = "Register New User (Public)", description = "Register a new user in the system. Access: All users")
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
        @SecurityRequirement(name = "none")
        @ResponseStatus(HttpStatus.CREATED)
        @PostMapping("/register")
        UserDto register(@RequestBody @Valid NewUserDto newUserDto);

        @PreAuthorize("hasRole('MASTER')")
        @Operation(summary = "Update or add user details. Available to all authorized masters.",
                description = "Available to all authorized masters. Updates or adds master details.")
        @ApiResponses({
                @ApiResponse(responseCode = "200",
                        description = "User details updated successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = UserDetailsDto.class))),
                @ApiResponse(responseCode = "404",
                        description = "User not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @PutMapping("/{userId}/details")
        UserDetailsDto updateUserDetails(
                @Parameter(description = "ID of the user to be updated. Cannot be empty.", required = true)
                @PathVariable("userId") Long userId,
                @Parameter(description = "User details to be updated or added.", required = true)
                @RequestBody @Valid NewUserDetailsDto userDetailsDto);

        @Operation(summary = "Find users by category ID. Available to all users.",
                description = "Retrieve users associated with a specific category.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200",
                        description = "Successful operation",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = UserDetailsDto.class)))
        })
        @SecurityRequirement(name = "none")
        @GetMapping("/by-category/{categoryId}")
        public ResponseEntity<List<UserDetailsDto>> findUsersByCategoryId(
                @Parameter(description = "ID of the category to filter users by.", required = true)
                @PathVariable("categoryId") Long categoryId);

        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Get All Users (ADMIN)", description = "Get all users in the system. Access: ADMIN only")
        @ApiResponse(responseCode = "200",
                description = "Successful operation",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = UserDetailsDto.class)))
        @GetMapping
        List<UserDetailsDto> getAllUsers();

        @Operation(summary = "Get All Masters (Public)", description = "Get all master users in the system. Access: All users")
        @ApiResponses(value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successful operation",
                        headers = {
                                @Header(
                                        name = "Cache-Control",
                                        description = "Кеширование на 30 минут",
                                        schema = @Schema(type = "string", example = "max-age=1800")
                                )
                        },
                        content = @Content(
                                mediaType = "application/json",
                                schema = @Schema(implementation = UserDetailsDto.class, type = "array")
                        )
                )
        })
        @SecurityRequirement(name = "none")
        @GetMapping("/masters")
        ResponseEntity<List<UserDetailsDto>> getAllMasters();

        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Delete User (ADMIN)", description = "Delete a user from the system. Access: ADMIN only")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200",
                        description = "User deleted successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = StandardResponseDto.class))),
                @ApiResponse(responseCode = "404",
                        description = "User not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @DeleteMapping("/{id}")
        ResponseEntity<String> deleteById(
                @Parameter(description = "ID of the user to be deleted. Cannot be empty.", required = true)
                @PathVariable("id") Long id);

        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Confirm Master by Email (ADMIN)", description = "Confirm a master user by their email. Access: ADMIN only")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200",
                        description = "Master confirmed successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = StandardResponseDto.class))),
                @ApiResponse(responseCode = "404",
                        description = "Master not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @PostMapping("/confirm-master-by-email")
        ResponseEntity<String> confirmMasterByEmail(
                @Parameter(description = "Email of the master to be confirmed.", required = true) @RequestParam String email);
}
