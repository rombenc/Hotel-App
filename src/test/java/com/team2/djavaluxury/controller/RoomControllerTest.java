package com.team2.djavaluxury.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.djavaluxury.dto.request.RoomRequest;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.RoomResponse;
import com.team2.djavaluxury.service.inter.IRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RoomControllerTest {

    @Mock
    private IRoomService roomService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RoomController roomController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewRoom_Success() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "room.jpg", "image/jpeg", "image data".getBytes());
        String jsonRoom = "{\"name\": \"Luxury Room\", \"price\": 200.0}";

        RoomRequest roomRequest = new RoomRequest();
        roomRequest.setImage(image);

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId("1");


        when(objectMapper.readValue(jsonRoom, RoomRequest.class)).thenReturn(roomRequest);
        when(roomService.addNewRoom(roomRequest)).thenReturn(roomResponse);

        ResponseEntity<?> response = roomController.addNewRoom(image, jsonRoom);

        assertEquals(201, response.getStatusCodeValue());
        verify(roomService, times(1)).addNewRoom(roomRequest);
    }


    @Test
    void testGetAllRooms() {
        List<RoomResponse> roomResponses = Arrays.asList(new RoomResponse(), new RoomResponse());
        when(roomService.getAllRooms()).thenReturn(roomResponses);

        ResponseEntity<CommonResponse<List<RoomResponse>>> response = roomController.getAllRooms();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
        verify(roomService, times(1)).getAllRooms();
    }

    @Test
    void testGetRoomById() {
        String roomId = "1";
        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId(roomId);

        when(roomService.getRoomById(roomId)).thenReturn(roomResponse);

        ResponseEntity<CommonResponse<RoomResponse>> response = roomController.getRoomById(roomId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(roomId, response.getBody().getData().getId());
        verify(roomService, times(1)).getRoomById(roomId);
    }

    @Test
    void testDeleteRoom() {
        String roomId = "1";

        ResponseEntity<CommonResponse<String>> response = roomController.deleteRoom(roomId);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Room deleted successfully", response.getBody().getMessage());
        verify(roomService, times(1)).deleteRoom(roomId);
    }

    @Test
    void testGetAvailableRooms() {
        List<RoomResponse> availableRooms = Arrays.asList(new RoomResponse(), new RoomResponse());
        when(roomService.getAllAvailableRooms()).thenReturn(availableRooms);

        ResponseEntity<CommonResponse<List<RoomResponse>>> response = roomController.getAvailableRooms();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
        verify(roomService, times(1)).getAllAvailableRooms();
    }

    @Test
    void testGetAvailableRoomsByDateAndType() {
        LocalDate checkInDate = LocalDate.now();
        LocalDate checkOutDate = LocalDate.now().plusDays(2);
        String roomType = "Deluxe";

        List<RoomResponse> availableRooms = Arrays.asList(new RoomResponse(), new RoomResponse());
        when(roomService.getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomType))
                .thenReturn(availableRooms);

        ResponseEntity<CommonResponse<List<RoomResponse>>> response =
                roomController.getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomType);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
        verify(roomService, times(1)).getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomType);
    }
}
