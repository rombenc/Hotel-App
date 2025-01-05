package com.team2.djavaluxury.service.impl;

import com.team2.djavaluxury.utils.exception.OurException;
import com.team2.djavaluxury.dto.request.BookingRequest;
import com.team2.djavaluxury.dto.response.BookingResponse;
import com.team2.djavaluxury.entity.Booking;
import com.team2.djavaluxury.entity.Room;
import com.team2.djavaluxury.entity.User;
import com.team2.djavaluxury.repository.BookingRepository;
import com.team2.djavaluxury.repository.RoomRepository;
import com.team2.djavaluxury.repository.UserRepository;
import com.team2.djavaluxury.service.inter.IBookingService;
import com.team2.djavaluxury.utils.Utils;
import com.team2.djavaluxury.utils.exception.ResourceNotFoundException;
import com.team2.djavaluxury.utils.exception.ValidationException;
import com.team2.djavaluxury.utils.mapper.BookingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements IBookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BookingResponse saveBooking(String roomId, Long userId, BookingRequest bookingRequest) {

        try {
            // Validasi tanggal
            if (bookingRequest.getCheckInDate().isBefore(LocalDate.now())) {
                throw new ValidationException("Check-in date must be today or a future date", null);
            }
            if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
                throw new ValidationException("Check-out date must be at least 1 day after check-in date", null);
            }

            // Cek ketersediaan room
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found", null));

            // (Opsional) Validasi user jika user repository diaktifkan
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found ", null));

            // Cek apakah room sudah dibooking di tanggal yang sama
            List<Booking> existingBookings = bookingRepository.findBookingsForRoomInDateRange(
                    bookingRequest.getCheckInDate(),
                    bookingRequest.getCheckOutDate(),
                    roomId
            );
            if (!existingBookings.isEmpty()) {
                throw new ValidationException("Room is unavailable on the selected dates", null);
            }
            // Atur detail booking
            Booking booking = Booking.builder()
                    .bookingConfirmationCode(Utils.generateRandomConfirmationCode(10))
                    .checkInDate(bookingRequest.getCheckInDate()) // Gunakan checkInDate dari request
                    .checkOutDate(bookingRequest.getCheckOutDate()) // Gunakan checkOutDate dari request
                    .numOfAdults(bookingRequest.getNumOfAdults()) // Gunakan jumlah orang dewasa dari request
                    .numOfChildren(bookingRequest.getNumOfChildren()) // Gunakan jumlah Anak Kecil dari request
                    .totalNumOfGuest(bookingRequest.getNumOfAdults() + bookingRequest.getNumOfChildren())
                    .room(room)
                    .totalDays((int) ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getCheckOutDate()))
                    .user(user)
                    .build();
            bookingRepository.save(booking);

            return bookingMapper.toResponse(booking);

        } catch (IllegalArgumentException e) {
            throw new OurException("Invalid input: " + e.getMessage());
        } catch (OurException e) {
            throw new OurException("Error saving booking: " + e.getMessage());
        }
    }


    @Override
    public BookingResponse findBookingByConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode)
                .stream()
                .findFirst() // Mengambil elemen pertama jika ada
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new ValidationException("Booking not found with confirmation code: " + confirmationCode, null));
    }


    @Override
    public List<BookingResponse> findAll() {

        return bookingRepository.findAll().stream().map(bookingMapper::toResponse).toList();

    }

    @Override
    public BookingResponse cancelBooking(String bookingId) {

        // Cek apakah booking ada
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ValidationException("Booking not found with ID: " + bookingId, null));
        System.out.println("booking ID : " + booking.getId());

        // Hapus booking dari database
        bookingRepository.delete(booking);

        return null;

    }

}
