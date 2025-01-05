package com.team2.djavaluxury.service.inter;

import com.team2.djavaluxury.dto.request.RoomRequest;
import com.team2.djavaluxury.dto.response.RoomResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IRoomService {
        RoomResponse addNewRoom(RoomRequest roomRequest);
        List<String> getAllRoomTypes();
        List<RoomResponse> getAllRooms();
        void deleteRoom(String roomId);
        RoomResponse updateRoom(RoomRequest roomRequest);
        RoomResponse getRoomById(String roomId);
        List<RoomResponse> getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
        List<RoomResponse> getAllAvailableRooms();
}
