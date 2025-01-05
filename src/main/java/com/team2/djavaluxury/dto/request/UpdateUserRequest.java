package com.team2.djavaluxury.dto.request;

import com.team2.djavaluxury.constant.Role;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}