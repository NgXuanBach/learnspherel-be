package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.DangKyKhoaHocResponse;
import com.learnspherel.service.DangKyKhoaHocService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dang-ky-khoa-hoc")
public class DangKyKhoaHocController {

    private final DangKyKhoaHocService dangKyKhoaHocService;

    public DangKyKhoaHocController(DangKyKhoaHocService dangKyKhoaHocService) {
        this.dangKyKhoaHocService = dangKyKhoaHocService;
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<DangKyKhoaHocResponse>>> getOrderHistory(HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<List<DangKyKhoaHocResponse>> response = dangKyKhoaHocService.getOrderHistory(token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}