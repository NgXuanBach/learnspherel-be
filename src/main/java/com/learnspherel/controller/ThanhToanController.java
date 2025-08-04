package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.ThanhToanDto;
import com.learnspherel.service.ThanhToanService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thanh-toan")
public class ThanhToanController {

    private final ThanhToanService thanhToanService;

    public ThanhToanController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }

    @PostMapping("/paypal/create")
    public ResponseEntity<ApiResponse<String>> createPayment(
            @RequestBody List<Long> khoaHocIds,
            HttpServletRequest httpRequest) {
        String token = extractToken(httpRequest);
        ApiResponse<String> response = thanhToanService.createPayment(token, khoaHocIds);
        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
    }

    @GetMapping("/paypal/success")
    public ResponseEntity<ApiResponse<ThanhToanDto>> executePayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {
        ApiResponse<ThanhToanDto> response = thanhToanService.executePayment(paymentId, payerId);
//        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "http://127.0.0.1:5500/paypal-success.html?paymentId=" + paymentId + "&PayerID=" + payerId)
                .build();

    }

    @GetMapping("/paypal/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelPayment(
            @RequestParam("token") String token) {
        ApiResponse<Void> response = thanhToanService.cancelPayment(token);
//        return new ResponseEntity<>(response, HttpStatus.resolve(response.getStatusCode()));
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "http://127.0.0.1:5500/paypal-cancel.html")
//                .body(response)
                .build();

    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}