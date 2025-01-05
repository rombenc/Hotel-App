package com.team2.djavaluxury.dto.response;

import com.team2.djavaluxury.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private User user;
    private String message;
    private int statusCode;
}
