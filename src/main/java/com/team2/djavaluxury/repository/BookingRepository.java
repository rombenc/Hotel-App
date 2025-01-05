package com.team2.djavaluxury.repository;

import com.team2.djavaluxury.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.*;

public interface BookingRepository extends JpaRepository<Booking, String> {
    Optional<Booking> findByBookingConfirmationCode(String confirmationCode);

    // Query untuk mengecek jika ada booking yang overlap dengan tanggal check-in dan check-out
    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND (" +
            "(b.checkInDate <= :checkOutDate AND b.checkOutDate >= :checkInDate))")
    List<Booking> findBookingsForRoomInDateRange(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("roomId") String roomId
    );
}
