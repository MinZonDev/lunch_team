package com.lunch_team.controller;

import com.lunch_team.dto.*;
import com.lunch_team.dto.response.ApiResponse;
import com.lunch_team.entity.User;
import com.lunch_team.service.AuthService;
import com.lunch_team.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterDto registerDto) {
        User user = authService.register(registerDto);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("username", user.getUsername());
        responseData.put("name", user.getName());
        responseData.put("email", user.getEmail());

        return ResponseUtil.success("Đăng ký thành công", responseData);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginDto loginDto) {
        AuthResponse authResponse = authService.login(loginDto);
        return ResponseUtil.success("Đăng nhập thành công", authResponse);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileDto>> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserProfileDto profile = authService.getMyProfile(username);
        return ResponseUtil.success("Lấy thông tin cá nhân thành công", profile);
    }

    // Cập nhật thông tin cá nhân
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateMyProfile(
            @Valid @RequestBody UpdateProfileDto updateProfileDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        UserProfileDto updatedProfile = authService.updateMyProfile(username, updateProfileDto);
        return ResponseUtil.success("Cập nhật thông tin thành công", updatedProfile);
    }

    // Xem thông tin người dùng khác
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserProfileDto>> getUserProfile(@PathVariable Long userId) {
        UserProfileDto profile = authService.getUserProfile(userId);
        return ResponseUtil.success("Lấy thông tin người dùng thành công", profile);
    }

    // Xem danh sách tất cả người dùng (dành cho admin)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> getAllUsers() {
        List<UserProfileDto> users = authService.getAllUsers();
        return ResponseUtil.success("Lấy danh sách người dùng thành công", users);
    }

    // Tìm kiếm người dùng
    @GetMapping("/users/search")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserProfileDto>>> searchUsers(
            @RequestParam String keyword) {
        List<UserProfileDto> users = authService.searchUsers(keyword);
        return ResponseUtil.success("Tìm kiếm người dùng thành công", users);
    }
}