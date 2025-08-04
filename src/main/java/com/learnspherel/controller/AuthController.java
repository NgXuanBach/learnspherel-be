package com.learnspherel.controller;

import com.learnspherel.dto.*;
import com.learnspherel.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> dangKy(
            @Valid @RequestPart("request") RegisterRequest registerRequest,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
        ApiResponse response = authService.register(registerRequest, avatar);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> dangNhap(@Valid @RequestBody LoginRequest loginRequest) {
        ApiResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> layThongTin(HttpServletRequest request) {
        String token = extractToken(request);
        ApiResponse response = authService.getCurrentUser(token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> capNhatThongTin(
            @Valid @RequestPart("request") UpdateUserRequest updateRequest,
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpServletRequest request) {
        String token = extractToken(request);
        ApiResponse<UserResponse> response = authService.updateCurrentUser(updateRequest, file, token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ApiResponse<Void> response = authService.forgotPassword(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ApiResponse<Void> response = authService.resetPassword(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequest request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<UserResponse> response = authService.updateUserRole(id, request, token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<List<UserResponse>> response = authService.getAllUsers(token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<Void> response = authService.deleteUser(id, token);
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