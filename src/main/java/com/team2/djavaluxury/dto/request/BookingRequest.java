package com.team2.djavaluxury.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team2.djavaluxury.dto.request.RoomRequest;
import com.team2.djavaluxury.entity.Room;
import com.team2.djavaluxury.entity.User;
import lombok.*;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingRequest {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numOfAdults;
    private int numOfChildren; 
}