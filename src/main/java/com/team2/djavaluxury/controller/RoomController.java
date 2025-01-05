package com.team2.djavaluxury.controller;

import com.team2.djavaluxury.dto.request.RoomRequest;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.RoomResponse;
import com.team2.djavaluxury.service.inter.IRoomService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/rooms")
public class RoomController {
    private final IRoomService roomService;
    private final ObjectMapper objectMapper;

    public RoomController(IRoomService roomService, ObjectMapper objectMapper) {
        this.roomService = roomService;
        this.objectMapper = objectMapper;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> addNewRoom(
            @RequestPart("image") MultipartFile image,
            @RequestPart("room") String jsonRoom
//            @ModelAttribute("room") String jsonRoom
    ) {
        try {
            RoomRequest request = objectMapper.readValue(jsonRoom, RoomRequest.class);
            request.setImage(image);
            RoomResponse response = roomService.addNewRoom(request);
            CommonResponse<RoomResponse> commonResponse = CommonResponse.<RoomResponse>builder()
                    .statusCode(HttpStatus.CREATED.value())
                    .message("Room successfully added")
                    .data(response)
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
//        } catch (ConstraintViolationException e) {
        } catch (JsonProcessingException e) {
            log.error("error: {}", e.getLocalizedMessage());
            CommonResponse<RoomResponse> errorResponse = CommonResponse.<RoomResponse>builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Failed to add room: " + e.getMessage())
                    .data(null)
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping(path = "/all",
             produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<List<RoomResponse>>> getAllRooms() {
        List<RoomResponse> rooms = roomService.getAllRooms();
        CommonResponse<List<RoomResponse>> response = CommonResponse.<List<RoomResponse>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("Rooms retrieved successfully")
                .data(rooms)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/types",
             produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<String>>> getRoomTypes() {
        List<String> roomTypes = roomService.getAllRoomTypes();
        CommonResponse<List<String>> response = CommonResponse.<List<String>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("Room types retrieved successfully")
                .data(roomTypes)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/room-by-id/{roomId}",
             produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<RoomResponse>> getRoomById(@PathVariable String roomId) {
        RoomResponse roomResponse = roomService.getRoomById(roomId);
        CommonResponse<RoomResponse> response = CommonResponse.<RoomResponse>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("Room retrieved successfully")
                .data(roomResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/all-available-rooms",
             produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<RoomResponse>>> getAvailableRooms() {
        List<RoomResponse> rooms = roomService.getAllAvailableRooms();
        CommonResponse<List<RoomResponse>> response = CommonResponse.<List<RoomResponse>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("Available rooms retrieved successfully")
                .data(rooms)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/available-rooms-by-date-and-type",
             produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<RoomResponse>>> getAvailableRoomsByDateAndType(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam String roomType
    ) {
        List<RoomResponse> rooms = roomService.getAvailableRoomsByDateAndType(checkInDate, checkOutDate, roomType);
        CommonResponse<List<RoomResponse>> response = CommonResponse.<List<RoomResponse>>builder()
                .statusCode(HttpStatus.FOUND.value())
                .message("Available rooms by date and type retrieved successfully")
                .data(rooms)
                .build();
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(path = "/update/{roomId}",
    consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<RoomResponse>> updateRoom(
            @PathVariable String roomId,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestPart("room") String jsonRoom
    ) {
        try {
            RoomRequest roomRequest = objectMapper.readValue(jsonRoom, new TypeReference<RoomRequest>() {
            });
            roomRequest.setId(roomId);
            if (image != null && !image.isEmpty()) {
                roomRequest.setImage(image);
            }
            RoomResponse roomResponse = roomService.updateRoom(roomRequest);
            CommonResponse<RoomResponse> response = CommonResponse.<RoomResponse>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Room updated successfully")
                    .data(roomResponse)
                    .build();
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            log.error("error: {}", e.getLocalizedMessage());
            CommonResponse<RoomResponse> errorResponse = CommonResponse.<RoomResponse>builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to update room: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping(path = "/delete/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<String>> deleteRoom(@PathVariable String roomId) {
        roomService.deleteRoom(roomId);
        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(200)
                .message("Room deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
