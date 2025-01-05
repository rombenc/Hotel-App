package com.team2.djavaluxury.controller;

import com.team2.djavaluxury.dto.request.*;
import com.team2.djavaluxury.dto.response.*;
import com.team2.djavaluxury.service.impl.AuthenticationServiceImpl;
import com.team2.djavaluxury.service.inter.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<RegisterResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/verify")
    public ResponseEntity<CommonResponse<String>> verifyUser(@RequestParam String email, @RequestParam String code) {
        return ResponseEntity.ok(authenticationService.verifyUser(email, code));
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<RefreshTokenResponse>> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }
}
