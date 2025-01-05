package com.team2.djavaluxury.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private Long id;
    private String fullName;
    private String email;
    private String message;
    private int statusCode;
    private String error;
    private String role;
    private String verificationCode;
}