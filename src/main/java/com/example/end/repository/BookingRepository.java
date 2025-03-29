package com.example.end.repository;

import com.example.end.models.Booking;
import com.example.end.models.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b INNER JOIN FETCH b.client LEFT JOIN FETCH b.master WHERE b.client.id = :userId OR b.master.id = :userId")
    List<Booking> findActiveBookingsWithClientAndMasterByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Booking b INNER JOIN FETCH b.client LEFT JOIN FETCH b.master WHERE (b.client.id = :userId OR b.master.id = :userId) AND b.status = :status")
    List<Booking> findBookingsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);

    boolean existsByClientIdAndMasterIdAndStatus(Long clientId, Long masterId, BookingStatus status);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b WHERE b.master.id = :masterId AND b.dateTime = :startTime")
    boolean existsOverlappingBooking(@Param("masterId") Long masterId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT b FROM Booking b WHERE b.client.id = :userId")
    List<Booking> findByUserId(@Param("userId") Long userId);
}