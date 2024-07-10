package com.example.end.controller;

import com.example.end.controller.api.BookingApi;
import com.example.end.dto.*;
import com.example.end.models.BookingStatus;
import com.example.end.service.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class BookingController implements BookingApi {

    private final BookingService bookingService;


    @Override
    public BookingDto createBooking(NewBookingDto bookingDto) {
        return bookingService.createBooking(bookingDto);
    }

    @Override
    public void updateBookingStatus(NewUpdateBookingDto bookingDto) {
        bookingService.updateBookingStatus(bookingDto);
    }

    @Override
    public void cancelBooking(Long bookingId) {
        bookingService.cancelBooking(bookingId);
    }

    @Override
    public List<BookingDto> findBookingsByUser(Long userId, BookingStatus status) {
       return bookingService.findBookingsByUser(userId, status);
    }


}