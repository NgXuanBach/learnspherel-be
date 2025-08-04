package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.TienDoHocTapDto;
import com.learnspherel.service.TienDoHocTapService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tien-do-hoc-tap")
public class TienDoHocTapController {

    private final TienDoHocTapService tienDoHocTapService;

    public TienDoHocTapController(TienDoHocTapService tienDoHocTapService) {
        this.tienDoHocTapService = tienDoHocTapService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> updateTienDoHocTap(
            @Valid @RequestBody TienDoHocTapDto request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<Void> response = tienDoHocTapService.updateTienDoHocTap(request, token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @GetMapping("/hoan-thanh/{maKhoaHoc}")
    public ResponseEntity<ApiResponse<Boolean>> checkHoanThanhKhoaHoc(
            @PathVariable long maKhoaHoc,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<Boolean> response = tienDoHocTapService.checkHoanThanKhoaHoc(maKhoaHoc, token);
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