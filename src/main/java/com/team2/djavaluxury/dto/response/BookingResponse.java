package com.team2.djavaluxury.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team2.djavaluxury.entity.Room;
import com.team2.djavaluxury.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {
    private String id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String bookingConfirmationCode;
    private int numOfAdults;
    private int numOfChildren;
    private int totalDays;
//    private Payment payment;
    private Room room;
    private User user;
}
