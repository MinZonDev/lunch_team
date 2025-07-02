package com.lunch_team.controller;

import com.lunch_team.dto.response.ApiResponse;
import com.lunch_team.exception.BusinessException;
import com.lunch_team.util.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/example")
public class ExampleController {

    // Ví dụ success response với data
    @GetMapping("/success")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSuccess() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello World");
        data.put("timestamp", System.currentTimeMillis());

        return ResponseUtil.success("Lấy dữ liệu thành công", data);
    }

    // Ví dụ success response không có data
    @GetMapping("/simple")
    public ResponseEntity<ApiResponse<Object>> getSimple() {
        return ResponseUtil.success("Thao tác thành công", null);
    }

    // Ví dụ created response
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Map<String, Object> request) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", 1);
        data.put("created", true);

        return ResponseUtil.created("Tạo mới thành công", data);
    }

    // Ví dụ business exception (sẽ được GlobalExceptionHandler xử lý)
    @GetMapping("/business-error")
    public ResponseEntity<ApiResponse<Object>> businessError() {
        throw BusinessException.notFound("Không tìm thấy tài nguyên");
    }

    // Ví dụ validation error (thông qua manual validation)
    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Object>> validate(@RequestBody Map<String, String> request) {
        Map<String, String> errors = new HashMap<>();

        if (!request.containsKey("name") || request.get("name").isEmpty()) {
            errors.put("name", "Tên không được để trống");
        }

        if (!request.containsKey("email") || request.get("email").isEmpty()) {
            errors.put("email", "Email không được để trống");
        }

        if (!errors.isEmpty()) {
            return ResponseUtil.badRequest("Dữ liệu không hợp lệ", errors);
        }

        return ResponseUtil.success("Validation thành công", request);
    }

    // Ví dụ các loại error khác
    @GetMapping("/unauthorized")
    public ResponseEntity<ApiResponse<Object>> unauthorized() {
        return ResponseUtil.unauthorized("Bạn không có quyền truy cập");
    }

    @GetMapping("/forbidden")
    public ResponseEntity<ApiResponse<Object>> forbidden() {
        return ResponseUtil.forbidden("Truy cập bị từ chối");
    }

    @GetMapping("/not-found")
    public ResponseEntity<ApiResponse<Object>> notFound() {
        return ResponseUtil.notFound("Không tìm thấy tài nguyên");
    }

    @GetMapping("/conflict")
    public ResponseEntity<ApiResponse<Object>> conflict() {
        return ResponseUtil.conflict("Dữ liệu đã tồn tại");
    }
}