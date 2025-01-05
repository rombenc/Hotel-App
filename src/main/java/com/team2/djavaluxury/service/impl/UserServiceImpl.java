package com.team2.djavaluxury.service.impl;

import java.util.Optional;

import com.team2.djavaluxury.constant.Role;
import com.team2.djavaluxury.dto.request.UpdateUserRequest;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.UpdateUserResponse;
import com.team2.djavaluxury.dto.response.UserResponse;
import com.team2.djavaluxury.entity.User;
import com.team2.djavaluxury.service.inter.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.team2.djavaluxury.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CommonResponse<UpdateUserResponse> updateUser(Long userId, UpdateUserRequest updatedUser) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User existingUser = userOptional.get();
                updateUserDetails(existingUser, updatedUser);
                User savedUser = userRepository.save(existingUser);

                UpdateUserResponse response = UpdateUserResponse.builder()
                        .user(savedUser)
                        .message("User updated successfully")
                        .build();

                return new CommonResponse<>(200, "Success", response);
            } else {
                return new CommonResponse<>(404, "User not found for update", null);
            }
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    public CommonResponse<UserResponse> getMyInfo(String email) {
        try {
            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                UserResponse response = UserResponse.builder()
                        .user(userOptional.get())
                        .message("Successfully retrieved user info")
                        .build();

                return new CommonResponse<>(200, "Success", response);
            } else {
                return new CommonResponse<>(404, "User not found", null);
            }
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    public void updateUserDetails(User existingUser, UpdateUserRequest updatedUser) {
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setRole(Role.valueOf(updatedUser.getRole().toString())); // Konversi enum ke string

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
    }

    private <T> CommonResponse<T> buildErrorResponse(Exception e) {
        return new CommonResponse<>(500, "Error: " + e.getMessage(), null);
    }
}
