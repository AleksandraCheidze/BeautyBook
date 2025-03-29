package com.example.end.mapping;

import com.example.end.dto.BookingDto;
import com.example.end.models.Booking;
import org.springframework.stereotype.Service;

@Service
public class BookingMapper {

    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .clientId(booking.getClient().getId())
                .masterId(booking.getMaster().getId())
                .procedureId(booking.getProcedure().getId())
                .dateTime(String.valueOf(booking.getDateTime()))
                .status(booking.getStatus())
                .build();
    }
}