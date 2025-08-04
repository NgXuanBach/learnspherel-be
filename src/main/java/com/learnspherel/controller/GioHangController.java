package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.GioHangDto;
import com.learnspherel.service.GioHangService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gio-hang")
public class GioHangController {

    private final GioHangService gioHangService;

    public GioHangController(GioHangService gioHangService) {
        this.gioHangService = gioHangService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GioHangDto>> addToCart(
            @Valid @RequestBody GioHangDto request,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<GioHangDto> response = gioHangService.addToCart(request, token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GioHangDto>>> getCartByUser(HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<List<GioHangDto>> response = gioHangService.getCartByUser(token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GioHangDto>> getCartItem(@PathVariable Long id) {
        ApiResponse<GioHangDto> response = gioHangService.getCartItem(id);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCartItem(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<Void> response = gioHangService.deleteCartItem(id, token);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<Void> response = gioHangService.clearCart(token);
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