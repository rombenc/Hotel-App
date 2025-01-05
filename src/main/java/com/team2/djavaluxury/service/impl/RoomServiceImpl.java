package com.team2.djavaluxury.service.impl;

import com.team2.djavaluxury.dto.request.RoomRequest;
import com.team2.djavaluxury.dto.response.RoomResponse;
import com.team2.djavaluxury.dto.response.ImageResponse;
import com.team2.djavaluxury.entity.Image;
import com.team2.djavaluxury.entity.Room;
import com.team2.djavaluxury.utils.exception.FileStorageException;
import com.team2.djavaluxury.utils.exception.RoomNotFoundException;
import com.team2.djavaluxury.repository.RoomRepository;
import com.team2.djavaluxury.service.inter.IRoomService;
import com.team2.djavaluxury.service.inter.ImageService;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements IRoomService {

    private final RoomRepository roomRepository;
    private final ImageService imageService;


    @Override
    public RoomResponse addNewRoom(RoomRequest roomRequest) {
        try {

            Room newRoom = new Room();
            newRoom.setRoomNumber(roomRequest.getRoomNumber());
            newRoom.setType(roomRequest.getType());
            newRoom.setPricePerNight(roomRequest.getPricePerNight());
            newRoom.setStatus("available");

            if (roomRequest.getImage() != null && !roomRequest.getImage().isEmpty()) {
                Image image = imageService.create(roomRequest.getImage());
                newRoom.setImage(image);
            }

            Room savedRoom = roomRepository.save(newRoom);
            return mapToRoomResponse(savedRoom);
        } catch (ConstraintViolationException e) {
            throw new RuntimeException("Invalid image type: " + e.getMessage());
        } catch (FileStorageException e) {
            throw new RuntimeException("Error saving room image: " + e.getMessage());
        }catch (Exception e) {
            throw new RuntimeException("Error saving room : " + e.getMessage());

        }
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }


    @Override
    public List<RoomResponse> getAllRooms() {
        List<Room> rooms = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return rooms.stream().map(this::mapToRoomResponse).collect(Collectors.toList());
    }
    @Override
    public void deleteRoom(String roomId) {
        try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new RoomNotFoundException("Room not found", null));
            roomRepository.deleteById(roomId);
        } catch (RoomNotFoundException e) {
            throw new RuntimeException("Room not found: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting room: " + e.getMessage());
        }
    }

    @Override
    public RoomResponse updateRoom(RoomRequest roomRequest) {
        Room currentRoom= findById(roomRequest.getId());
        currentRoom.setRoomNumber(roomRequest.getRoomNumber());
        currentRoom.setType(roomRequest.getType());
        currentRoom.setPricePerNight(roomRequest.getPricePerNight());

        if (roomRequest.getImage() != null && !roomRequest.getImage().isEmpty()) {
            if (currentRoom.getImage() != null) {
                roomRepository.delete(currentRoom);
                imageService.deleteById(currentRoom.getImage().getId());
            }
            Image image = imageService.create(roomRequest.getImage());
            currentRoom.setImage(image);
        }

        roomRepository.saveAndFlush(currentRoom);
        return mapToRoomResponse(currentRoom);

    }

    @Override
    public RoomResponse getRoomById(String roomId) {
        try {
            Room room = roomRepository.findById(roomId).
                    orElseThrow(() -> new RoomNotFoundException("Room not found", null));
            return mapToRoomResponse(room);
        } catch (RoomNotFoundException e) {
            throw new RuntimeException("Room not found: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving room: " + e.getMessage());
        }
    }

    @Override
    public List<RoomResponse> getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        List<Room> rooms = roomRepository.findAvailableRoomsByDatesAndTypes(checkInDate, checkOutDate, roomType);
        return rooms.stream().map(this::mapToRoomResponse).collect(Collectors.toList());
    }

    @Override
    public List<RoomResponse> getAllAvailableRooms() {
        List<Room> rooms = roomRepository.getAllAvailableRooms();
        return rooms.stream().map(this::mapToRoomResponse).collect(Collectors.toList());
    }

    private Room findById(String roomId) {
        return roomRepository.findById(roomId).
                orElseThrow(() -> new RoomNotFoundException("Room not found", null));
    }

    private RoomResponse mapToRoomResponse(Room room) {
        boolean isAvailable = room.getBooking() != null && room.getBooking().stream().noneMatch(
                book -> {
                    LocalDate checkIn = book.getCheckInDate();
                    LocalDate checkOut = book.getCheckOutDate();
                    return checkIn != null && checkOut != null &&
                            checkIn.isBefore(LocalDate.now()) && checkOut.isAfter(LocalDate.now());
                }
        );
        ImageResponse imageResponse = room.getImage() != null ? ImageResponse.builder()
                .url("/assets/images/" + room.getImage().getName())
                .name(room.getImage().getName())
                .build() : null;
        return RoomResponse.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .type(room.getType())
                .pricePerNight(room.getPricePerNight())
                .status(room.getStatus())
                .image(imageResponse)
                .build();
    }
}
