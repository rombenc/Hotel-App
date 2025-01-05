package com.team2.djavaluxury.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteUserResponse {
    private String message;
    private int statusCode;
}
