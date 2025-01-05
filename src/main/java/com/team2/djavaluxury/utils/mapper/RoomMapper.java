package com.team2.djavaluxury.utils.mapper;

import com.team2.djavaluxury.dto.response.BookingResponse;
import com.team2.djavaluxury.dto.response.RoomResponse;
import com.team2.djavaluxury.entity.Booking;
import com.team2.djavaluxury.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Named("RoomMapper")

public interface RoomMapper {
    RoomResponse toResponse(Room room);
    Room toEntity(RoomResponse roomResponse);
    List<RoomResponse> toResponseList(List<Room> roomList);
}