package com.team2.djavaluxury.controller;

import com.team2.djavaluxury.constant.Role;
import com.team2.djavaluxury.dto.request.LoginRequest;
import com.team2.djavaluxury.dto.request.RegisterRequest;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.LoginResponse;
import com.team2.djavaluxury.dto.response.RegisterResponse;
import com.team2.djavaluxury.entity.User;
import com.team2.djavaluxury.repository.UserRepository;
import com.team2.djavaluxury.security.JwtService;
import com.team2.djavaluxury.service.impl.AuthenticationServiceImpl;
import com.team2.djavaluxury.service.impl.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;


@ExtendWith(MockitoExtension.class)
class UserAuthControllerTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private EmailService emailService;

    @Test
    void testRegisterSuccess() throws MessagingException {
        RegisterRequest request = new RegisterRequest(
                "dikibulin",
                "Diki",
                "Bulin",
                "dikibulin@mail.com",
                "dikibulin123",
                Role.USER);
        User mockUser = User.builder()
                .id(1L)
                .username("user123")
                .email("user@example.com")
                .role(Role.USER)
                .firstName("John")
                .lastName("Doe")
                .password("encodedPassword")
                .verificationCode("123456")
                .isVerified(false)
                .build();

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        CommonResponse<RegisterResponse> response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Success", response.getMessage());
        assertEquals("user@example.com", response.getData().getEmail());
        verify(emailService, times(1)).sendVerificationEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testRegisterFailure() {
        RegisterRequest request = new RegisterRequest(
                "user123",
                "",
                "Bulin",
                "dikibulin@mail.com",
                "dikibulin123",
                Role.USER);
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        CommonResponse<RegisterResponse> response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Error"));
    }

    @Test
    void testLoginSuccess() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");
        User mockUser = User.builder()
                .email("user@example.com")
                .password("encodedPassword")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn("mockJwtToken");
        when(jwtService.generateRefreshToken(any(), eq(mockUser))).thenReturn("mockRefreshToken");

        doAnswer(invocationOnMock -> null).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        CommonResponse<LoginResponse> response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Success", response.getMessage());
        assertEquals("mockJwtToken", response.getData().getToken());
    }

    @Test
    void testLoginInvalidCredentials() {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "wrongPassword");

        doThrow(new RuntimeException("Bad credentials"))
                .when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        CommonResponse<LoginResponse> response = authenticationService.login(loginRequest);

        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Error"));
    }

    @Test
    void testVerifyUserSuccess() {
        String email = "user@example.com";
        String verificationCode = "123456";
        User mockUser = User.builder()
                .email(email)
                .verificationCode(verificationCode)
                .isVerified(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        CommonResponse<String> response = authenticationService.verifyUser(email, verificationCode);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("Email verified successfully", response.getMessage());
        assertEquals("Verified", response.getData());
        assertNull(mockUser.getVerificationCode());
        assertTrue(mockUser.isVerified());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testVerifyUserInvalidCode() {
        String email = "user@example.com";
        String verificationCode = "wrongCode";
        User mockUser = User.builder()
                .email(email)
                .verificationCode("123456")
                .isVerified(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        CommonResponse<String> response = authenticationService.verifyUser(email, verificationCode);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
        assertEquals("Invalid verification code", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testVerifyUserUserNotFound() {
        String email = "user@example.com";
        String verificationCode = "123456";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        CommonResponse<String> response = authenticationService.verifyUser(email, verificationCode);

        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getMessage().contains("Error"));
    }
}
