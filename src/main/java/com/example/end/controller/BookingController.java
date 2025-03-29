package com.example.end.controller;

import com.example.end.controller.api.BookingApi;
import com.example.end.dto.*;
import com.example.end.models.BookingStatus;
import com.example.end.service.interfaces.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Bookings", description = "API endpoints for booking management")
public class BookingController implements BookingApi {

    private final BookingService bookingService;

    @Override
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create Booking (Authorized)", description = "Create a new booking in the system. Access: All authorized users")
    @SecurityRequirement(name = "bearerAuth")
    public BookingDto createBooking(NewBookingDto bookingDto) {
        return bookingService.createBooking(bookingDto);
    }

    @Override
    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Booking Status (ADMIN)", description = "Update the status of a booking. Access: ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    public void updateBookingStatus(NewUpdateBookingDto bookingDto) {
        bookingService.updateBookingStatus(bookingDto);
    }

    @Override
    @PatchMapping("/{bookingId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel Booking (Authorized)", description = "Cancel a booking. Access: All authorized users")
    @SecurityRequirement(name = "bearerAuth")
    public void cancelBooking(Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    @Override
    @GetMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get User Bookings (Authorized)", description = "Get all bookings for a specific user, optionally filtered by status. Access: All authorized users")
    @SecurityRequirement(name = "bearerAuth")
    public List<BookingDto> findBookingsByUser(Long userId, BookingStatus status) {
        return bookingService.findBookingsByUser(userId, status);
    }
}