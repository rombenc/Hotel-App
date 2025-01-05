package com.team2.djavaluxury.service.impl;

import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.DeleteUserResponse;
import com.team2.djavaluxury.dto.response.GetAllUsersResponse;
import com.team2.djavaluxury.dto.response.UserResponse;
import com.team2.djavaluxury.entity.User;
import com.team2.djavaluxury.repository.BookingRepository;
import com.team2.djavaluxury.repository.RoomRepository;
import com.team2.djavaluxury.repository.UserRepository;
import com.team2.djavaluxury.service.inter.AdminService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public CommonResponse<DeleteUserResponse> deleteUser(Long userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                userRepository.deleteById(userId);

                DeleteUserResponse response = DeleteUserResponse.builder()
                        .message("User deleted successfully")
                        .build();

                return new CommonResponse<>(200, "Success", response);
            } else {
                return new CommonResponse<>(404, "User not found for deletion", null);
            }
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    private <T> CommonResponse<T> buildErrorResponse(Exception e) {
        return new CommonResponse<>(500, "Error: " + e.getMessage(), null);
    }

    public CommonResponse<GetAllUsersResponse> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();

            if (!users.isEmpty()) {
                GetAllUsersResponse response = GetAllUsersResponse.builder()
                        .users(users)
                        .message("Users retrieved successfully")
                        .build();

                return new CommonResponse<>(200, "Success", response);
            } else {
                return new CommonResponse<>(404, "No users found", null);
            }
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    public CommonResponse<UserResponse> getUserById(Long id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

            UserResponse response = UserResponse.builder()
                    .user(user)
                    .message("User found successfully")
                    .build();

            return new CommonResponse<>(200, "Success", response);
        } catch (Exception e) {
            return buildErrorResponse(e);
        }
    }

    @Override
    public void exportStatistics(OutputStream outputStream) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Statistics");

        // Header
        int rowNum = 0;
        Row headerRow = sheet.createRow(rowNum++);
        headerRow.createCell(0).setCellValue("Entity");
        headerRow.createCell(1).setCellValue("Details");
        headerRow.createCell(2).setCellValue("Count");

        // Room statistics
        long totalRooms = roomRepository.count();
        Row roomRow = sheet.createRow(rowNum++);
        roomRow.createCell(0).setCellValue("Rooms");
        roomRow.createCell(1).setCellValue("Total rooms in database");
        roomRow.createCell(2).setCellValue(totalRooms);

        // Booking statistics
        long totalBookings = bookingRepository.count();
        Row bookingRow = sheet.createRow(rowNum++);
        bookingRow.createCell(0).setCellValue("Bookings");
        bookingRow.createCell(1).setCellValue("Total bookings in database");
        bookingRow.createCell(2).setCellValue(totalBookings);

        // Write to output stream
        workbook.write(outputStream);
        workbook.close();
    }
}
