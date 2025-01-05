package com.team2.djavaluxury.dto.request;

import com.team2.djavaluxury.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
}