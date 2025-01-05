package com.team2.djavaluxury.dto.response;

import com.team2.djavaluxury.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetAllUsersResponse {
    private List<User> users;
    private String message;
    private int statusCode;
}
