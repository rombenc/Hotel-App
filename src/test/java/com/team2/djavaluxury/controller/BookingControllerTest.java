package com.team2.djavaluxury.controller;

import com.team2.djavaluxury.dto.request.BookingRequest;
import com.team2.djavaluxury.dto.response.BookingResponse;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.service.inter.IBookingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    @Mock
    private IBookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    public BookingControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveBookings() {
        String roomId = "room123";
        Long userId = 1L;
        BookingRequest bookingRequest = new BookingRequest();
        BookingResponse mockResponse = new BookingResponse();

        when(bookingService.saveBooking(roomId, userId, bookingRequest)).thenReturn(mockResponse);

        ResponseEntity<CommonResponse<BookingResponse>> response = bookingController.saveBookings(roomId, userId, bookingRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody().getData());
        verify(bookingService, times(1)).saveBooking(roomId, userId, bookingRequest);
    }

    @Test
    void testGetAllBookings() {
        List<BookingResponse> mockResponseList = Collections.singletonList(new BookingResponse());

        when(bookingService.findAll()).thenReturn(mockResponseList);

        ResponseEntity<CommonResponse<List<BookingResponse>>> response = bookingController.getAllBookings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponseList, response.getBody().getData());
        verify(bookingService, times(1)).findAll();
    }

    @Test
    void testGetBookingByConfirmationCode() {
        String confirmationCode = "ABC123";
        BookingResponse mockResponse = new BookingResponse();

        when(bookingService.findBookingByConfirmationCode(confirmationCode)).thenReturn(mockResponse);

        ResponseEntity<CommonResponse<BookingResponse>> response = bookingController.getBookingByConfirmationCode(confirmationCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody().getData());
        verify(bookingService, times(1)).findBookingByConfirmationCode(confirmationCode);
    }

    @Test
    void testCancelBooking() {
        String bookingId = "sstring";
        BookingResponse mockResponse = new BookingResponse();

        when(bookingService.cancelBooking(bookingId)).thenReturn(mockResponse);

        ResponseEntity<CommonResponse<BookingResponse>> response = bookingController.cancelBooking(bookingId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockResponse, response.getBody().getData());
        verify(bookingService, times(1)).cancelBooking(bookingId);
    }
}
