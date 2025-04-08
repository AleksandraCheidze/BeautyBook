package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.infrastructure.exceptions.ResourceNotFoundException;
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

/**
 * Implementation of the BookingService interface.
 * Provides business logic for creating, updating, canceling, and retrieving bookings.
 */
@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final ProcedureRepository procedureRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new booking for a client with a specific master and procedure.
     *
     * @param bookingDto the DTO containing the booking details.
     * @return the created BookingDto.
     * @throws  if the user or procedure is not found.
     */
    @Override
    public BookingDto createBooking(NewBookingDto bookingDto) {
        User client = userRepository.findById(bookingDto.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException ("Client not found"));

        User master = userRepository.findById(bookingDto.getMasterId())
                .orElseThrow(() -> new ResourceNotFoundException("Master not found"));

        Procedure procedure = procedureRepository.findById(bookingDto.getProcedureId())
                .orElseThrow(() -> new ResourceNotFoundException("Procedure not found"));

        Booking booking = new Booking();
        booking.setDateTime(LocalDateTime.parse(bookingDto.getDateTime()));
        booking.setClient(client);
        booking.setMaster(master);
        booking.setProcedure(procedure);
        booking.setStatus(BookingStatus.CONFIRMED);

        booking = bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    /**
     * Updates the status of an existing booking.
     *
     * @param bookingDto the DTO containing the booking ID and new status.
     * @throws IllegalArgumentException if the booking with the given ID is not found.
     */
    @Override
    public void updateBookingStatus(NewUpdateBookingDto bookingDto) {
        Booking existingBooking = bookingRepository.findById(bookingDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Booking with ID " + bookingDto.getId() + " not found"));

        existingBooking.setStatus(bookingDto.getStatus());
        bookingRepository.save(existingBooking);
    }

    /**
     * Cancels an existing booking by updating its status to CANCEL.
     *
     * @param bookingId the ID of the booking to cancel.
     */
    @Override
    public void cancelBooking(Long bookingId) {
        NewUpdateBookingDto bookingDto = new NewUpdateBookingDto();
        bookingDto.setId(bookingId);
        bookingDto.setStatus(BookingStatus.CANCELED);
        updateBookingStatus(bookingDto);
    }

    /**
     * Retrieves a list of bookings for a specific user with a specific status.
     *
     * @param userId the ID of the user for whom the bookings are to be retrieved.
     * @param status the status of the bookings to retrieve.
     * @return a list of BookingDto objects representing the user's bookings.
     */
    @Override
    public List<BookingDto> findBookingsByUser(Long userId, BookingStatus status) {
        List<Booking> bookings = bookingRepository.findBookingsByUserIdAndStatus(userId, status);
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
