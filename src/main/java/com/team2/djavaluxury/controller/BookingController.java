package com.team2.djavaluxury.controller;


import com.team2.djavaluxury.dto.request.BookingRequest;
import com.team2.djavaluxury.dto.response.BookingResponse;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.entity.Booking;
import com.team2.djavaluxury.service.inter.IBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final IBookingService bookingService;

    @PostMapping("/{roomId}/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<CommonResponse<BookingResponse>> saveBookings(@PathVariable String roomId,
                                                                        @PathVariable Long userId,
                                                                        @RequestBody BookingRequest bookingRequest) {

        BookingResponse bookingResponse = bookingService.saveBooking(roomId, userId, bookingRequest);
        CommonResponse<BookingResponse> response = CommonResponse.<BookingResponse>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("Rooms retrieved successfully")
                .data(bookingResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<List<BookingResponse>>> getAllBookings() {

        List<BookingResponse> bookingResponse = bookingService.findAll();

        CommonResponse<List<BookingResponse>> response = CommonResponse.<List<BookingResponse>>builder()
                .statusCode(!bookingResponse.isEmpty() ? HttpStatus.FOUND.value() : HttpStatus.NOT_FOUND.value())
                .message(!bookingResponse.isEmpty() ? "Success Get All Bookings ": "No Booking Found!")
                .data(bookingResponse)
                .build();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/get-by-confirmation-code/{confirmationCode}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<CommonResponse<BookingResponse>> getBookingByConfirmationCode(@PathVariable String confirmationCode) {

        BookingResponse bookingResponse = bookingService.findBookingByConfirmationCode(confirmationCode);
        CommonResponse<BookingResponse> response = CommonResponse.<BookingResponse>builder()
                .statusCode(bookingResponse != null ? HttpStatus.FOUND.value() : HttpStatus.NOT_FOUND.value())
                .message(bookingResponse != null ? "Success Get Booking by Confirmation Code" : "No Booking Found!")
                .data(bookingResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancel/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<CommonResponse<BookingResponse>> cancelBooking(@PathVariable String bookingId) {
        BookingResponse bookingResponse = bookingService.cancelBooking(bookingId);
        CommonResponse<BookingResponse> response = CommonResponse.<BookingResponse>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("Success Cancel Bookings")
                .data(bookingResponse)
                .build();
        return ResponseEntity.ok(response);
    }
}
