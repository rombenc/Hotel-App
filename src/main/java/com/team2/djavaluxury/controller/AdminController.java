package com.team2.djavaluxury.controller;

import com.team2.djavaluxury.dto.response.*;
import com.team2.djavaluxury.service.impl.AdminServiceImpl;
import com.team2.djavaluxury.service.inter.AdminService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/get-all-users")
    public ResponseEntity<CommonResponse<GetAllUsersResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/get-users/{userId}")
    public ResponseEntity<CommonResponse<UserResponse>> getUserByID(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getUserById(userId));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<CommonResponse<DeleteUserResponse>> deleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.deleteUser(userId));
    }

    @GetMapping("/download-statistics")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void downloadStatistics(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=statistics.xlsx");

        adminService.exportStatistics(response.getOutputStream());
    }
}
