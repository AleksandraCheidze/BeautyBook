package com.example.end.service.interfaces;

import com.example.end.dto.*;
import com.example.end.models.BookingStatus;
import com.example.end.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookingService {
    BookingDto createBooking(NewBookingDto bookingDto);

    void updateBookingStatus(NewUpdateBookingDto bookingDto);

    void cancelBooking(Long bookingId);

    List<BookingDto> findBookingsByUser(Long userId, BookingStatus status);
}
