package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.exceptions.ProcedureNotFoundException;
import com.example.end.exceptions.UserNotFoundException;
import com.example.end.mapping.BookingMapper;
import com.example.end.models.Booking;
import com.example.end.models.BookingStatus;
import com.example.end.models.Procedure;
import com.example.end.models.User;
import com.example.end.repository.BookingRepository;
import com.example.end.repository.ProcedureRepository;
import com.example.end.repository.UserRepository;
import com.example.end.service.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl  implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ProcedureRepository procedureRepository;
    private final UserRepository userRepository;



    @Override
    public BookingDto createBooking(NewBookingDto bookingDto) {
        User client = userRepository.findById(bookingDto.getClientId())
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        User master = userRepository.findById(bookingDto.getMasterId())
                .orElseThrow(() -> new UserNotFoundException("Master not found"));

        Procedure procedure = procedureRepository.findById(bookingDto.getProcedureId())
                .orElseThrow(() -> new ProcedureNotFoundException("Procedure not found"));

        Booking booking = new Booking();
        booking.setDateTime(LocalDateTime.parse(bookingDto.getDateTime()));
        booking.setClient(client);
        booking.setMaster(master);
        booking.setProcedure(procedure);
        booking.setStatus(BookingStatus.CONFIRMED);

        booking = bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Override
    public void updateBookingStatus(NewUpdateBookingDto bookingDto) {
        Booking existingBooking = bookingRepository.findById(bookingDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingDto.getId() + " not found"));

        existingBooking.setStatus(bookingDto.getStatus());
        bookingRepository.save(existingBooking);
    }


    @Override
    public void cancelBooking(Long bookingId) {
        NewUpdateBookingDto bookingDto = new NewUpdateBookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStatus(BookingStatus.CANCELED);
        updateBookingStatus(bookingDto);
    }

    @Override
    public List<BookingDto> findBookingsByUser(Long userId, BookingStatus status) {
        List<Booking> bookings = bookingRepository.findBookingsByUserIdAndStatus(userId, status);
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
}
}