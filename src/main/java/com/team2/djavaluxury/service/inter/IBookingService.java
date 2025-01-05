package com.team2.djavaluxury.service.inter;

import com.team2.djavaluxury.dto.request.BookingRequest;
import com.team2.djavaluxury.dto.response.BookingResponse;

import java.util.*;

public interface IBookingService {
    BookingResponse saveBooking(String roomId, Long userId, BookingRequest bookingRequest);

    BookingResponse findBookingByConfirmationCode(String confirmationCode);

    List<BookingResponse> findAll();

    BookingResponse cancelBooking(String bookingId);
}
