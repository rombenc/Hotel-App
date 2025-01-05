package com.team2.djavaluxury.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {
    @NotBlank
    private String id;
    @JsonProperty("roomNumber")
    private String roomNumber;
    @Min(value = 0, message = "Price must be positve")
    @JsonProperty("pricePerNight")
    private Double pricePerNight;
    @JsonProperty("type")
    private String type;
    private MultipartFile image;
}
