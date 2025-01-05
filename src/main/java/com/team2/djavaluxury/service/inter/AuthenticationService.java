package com.team2.djavaluxury.service.inter;

import com.team2.djavaluxury.dto.request.LoginRequest;
import com.team2.djavaluxury.dto.request.RefreshTokenRequest;
import com.team2.djavaluxury.dto.request.RegisterRequest;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.LoginResponse;
import com.team2.djavaluxury.dto.response.RefreshTokenResponse;
import com.team2.djavaluxury.dto.response.RegisterResponse;

public interface AuthenticationService {
    CommonResponse<RegisterResponse> register(RegisterRequest request);
    CommonResponse<String> verifyUser(String email, String verificationCode);
    CommonResponse<LoginResponse> login(LoginRequest loginRequest);
    CommonResponse<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest);
}
