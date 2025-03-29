package com.example.end.controller.api;

import com.example.end.dto.*;
import com.example.end.models.BookingStatus;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Bookings", description = "API endpoints for booking management")
@RequestMapping("/api/bookings")
@Tags(value = {
                @Tag(name = "Bookings", description = "Handling of bookings")
})
@ApiResponses(value = {
                @ApiResponse(responseCode = "401", description = "User is not authorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class)))
})
public interface BookingApi {
        @Operation(summary = "Create Booking (Authorized)", description = "Create a new booking in the system. Access: All authorized users")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Booking was successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookingDto.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))),
                        @ApiResponse(responseCode = "401", description = "User is not authorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @SecurityRequirement(name = "bearerAuth")
        BookingDto createBooking(@RequestBody @Valid NewBookingDto bookingDto);

        @Operation(summary = "Update Booking Status (ADMIN)", description = "Update the status of a booking. Access: ADMIN only")
        @PutMapping("/status")
        @SecurityRequirement(name = "bearerAuth")
        void updateBookingStatus(@RequestBody @Valid NewUpdateBookingDto bookingDto);

        @Operation(summary = "Cancel Booking (Authorized)", description = "Cancel a booking. Access: All authorized users")
        @PatchMapping("/{bookingId}")
        @SecurityRequirement(name = "bearerAuth")
        void cancelBooking(
                        @Parameter(description = "Booking identifier", example = "1", required = true) @PathVariable("bookingId") Long bookingId);

        @Operation(summary = "Get User Bookings (Authorized)", description = "Get all bookings for a specific user, optionally filtered by status. Access: All authorized users")
        @GetMapping("/{userId}")
        @SecurityRequirement(name = "bearerAuth")
        List<BookingDto> findBookingsByUser(
                        @Parameter(description = "User ID", example = "1", required = true) @PathVariable("userId") Long userId,
                        @Parameter(description = "Status of the booking (CONFIRMED or COMPLETED)", example = "CONFIRMED", required = false) @RequestParam(name = "status", required = false) BookingStatus status);
}