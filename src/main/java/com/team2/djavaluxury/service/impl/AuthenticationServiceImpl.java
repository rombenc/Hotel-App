package com.team2.djavaluxury.service.impl;

import com.team2.djavaluxury.constant.Role;
import com.team2.djavaluxury.dto.request.*;
import com.team2.djavaluxury.dto.response.*;
import com.team2.djavaluxury.entity.User;
import com.team2.djavaluxury.repository.UserRepository;
import com.team2.djavaluxury.security.JwtService;
import com.team2.djavaluxury.service.inter.AuthenticationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    public CommonResponse<RegisterResponse> register(RegisterRequest request) {
        try {
            User newUser = buildUserFromRequest(request);
            newUser.setVerificationCode(generateVerificationCode());
            newUser.setVerified(false); // Set verified false awalnya

            User savedUser = userRepository.save(newUser);
            sendVerificationEmail(savedUser); // Kirim email verifikasi

            RegisterResponse response = RegisterResponse.builder()
                    .id(savedUser.getId())
                    .email(savedUser.getEmail())
                    .role(String.valueOf(savedUser.getRole()))
                    .message("User saved successfully. Please verify your email.")
                    .verificationCode(savedUser.getVerificationCode())
                    .build();

            return new CommonResponse<>(200, "Success", response);
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    public CommonResponse<String> verifyUser(String email, String verificationCode) {
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            if (user.getVerificationCode().equals(verificationCode)) {
                user.setVerified(true);
                user.setVerificationCode(null); // Clear kode verifikasi setelah berhasil
                userRepository.save(user);
                return new CommonResponse<>(200, "Email verified successfully", "Verified");
            } else {
                return new CommonResponse<>(400, "Invalid verification code", null);
            }
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    public CommonResponse<LoginResponse> login(LoginRequest loginRequest) {
        try {
            authenticateUser(loginRequest);
            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            String jwt = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

            LoginResponse response = LoginResponse.builder()
                    .token(jwt)
                    .role(String.valueOf(user.getRole()))
                    .expirationTime("24Hrs")
                    .message("Successfully logged in")
                    .build();

            return new CommonResponse<>(200, "Success", response);
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    public CommonResponse<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            String email = jwtService.extractUsername(refreshTokenRequest.getToken());
            User user = userRepository.findByEmail(email).orElseThrow();

            if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
                String newToken = jwtService.generateToken(user);

                RefreshTokenResponse response = RefreshTokenResponse.builder()
                        .token(newToken)
                        .expirationTime("24Hr")
                        .message("Successfully refreshed token")
                        .build();

                return new CommonResponse<>(200, "Success", response);
            }
            return new CommonResponse<>(400, "Invalid token", null);
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // Generate 6 digit code
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html><body style=\"font-family: Arial, sans-serif;\">" +
                "<div style=\"background-color: #f5f5f5; padding: 20px;\">" +
                "<h2 style=\"color: #333;\">Welcome to our app!</h2>" +
                "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>" +
                "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">" +
                "<h3 style=\"color: #333;\">Verification Code:</h3>" +
                "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>" +
                "</div></div></body></html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private User buildUserFromRequest(RegisterRequest request) throws SQLException {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .role(Role.valueOf(request.getRole().toString())) // Konversi string ke enum
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    private void authenticateUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
    }

    private <T> CommonResponse<T> buildErrorResponse(Exception e) {
        return new CommonResponse<>(500, "Error: " + e.getMessage(), null);
    }
}
