package com.team2.djavaluxury.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String role;
    private String expirationTime;
    private String message;
    private int statusCode;
}