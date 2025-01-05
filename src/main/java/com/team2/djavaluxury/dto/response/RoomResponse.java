package com.team2.djavaluxury.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {
    private String id;
    private String roomNumber;
    private String type;
    private Double pricePerNight;
    private ImageResponse image;
    private String status;
}
