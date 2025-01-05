package com.team2.djavaluxury.service.inter;

import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.DeleteUserResponse;
import com.team2.djavaluxury.dto.response.GetAllUsersResponse;
import com.team2.djavaluxury.dto.response.UserResponse;

import java.io.IOException;
import java.io.OutputStream;

public interface AdminService {
    CommonResponse<DeleteUserResponse> deleteUser(Long userId);
    CommonResponse<GetAllUsersResponse> getAllUsers();
    CommonResponse<UserResponse> getUserById(Long id);
    void exportStatistics(OutputStream outputStream) throws IOException;
}
