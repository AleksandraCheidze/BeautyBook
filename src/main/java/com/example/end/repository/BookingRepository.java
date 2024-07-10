package com.example.end.repository;

import com.example.end.models.Booking;
import com.example.end.models.BookingStatus;
import com.example.end.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {



    @Query("SELECT b FROM Booking b INNER JOIN FETCH b.client LEFT JOIN FETCH b.master WHERE b.client.id = :userId OR b.master.id = :userId")
    List<Booking> findActiveBookingsWithClientAndMasterByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b INNER JOIN FETCH b.client LEFT JOIN FETCH b.master WHERE (b.client.id = :userId OR b.master.id = :userId) AND b.status = :status")
    List<Booking> findBookingsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);

}