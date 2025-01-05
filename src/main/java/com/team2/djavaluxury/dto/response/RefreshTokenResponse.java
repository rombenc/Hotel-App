package com.team2.djavaluxury.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenResponse {
    private String token;
    private String expirationTime;
    private String message;
    private int statusCode;
}