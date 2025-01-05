package com.team2.djavaluxury.service.inter;

import com.team2.djavaluxury.dto.request.UpdateUserRequest;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.UpdateUserResponse;
import com.team2.djavaluxury.dto.response.UserResponse;
import com.team2.djavaluxury.entity.User;

public interface UserService {
    CommonResponse<UpdateUserResponse> updateUser(Long userId, UpdateUserRequest updatedUser);
    CommonResponse<UserResponse> getMyInfo(String email);
    void updateUserDetails(User existingUser, UpdateUserRequest updatedUser);
}
