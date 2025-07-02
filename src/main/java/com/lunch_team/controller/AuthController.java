package com.lunch_team.controller;

import com.lunch_team.dto.AuthResponse;
import com.lunch_team.dto.LoginDto;
import com.lunch_team.dto.RegisterDto;
import com.lunch_team.dto.response.ApiResponse;
import com.lunch_team.entity.User;
import com.lunch_team.service.AuthService;
import com.lunch_team.util.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
}