package com.learnspherel.controller;

import com.learnspherel.dto.ApiResponse;
import com.learnspherel.dto.KyNangRequest;
import com.learnspherel.dto.KyNangResponse;
import com.learnspherel.service.KyNangService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ky-nang")
public class KyNangController {

    private final KyNangService kyNangService;

    public KyNangController(KyNangService kyNangService) {
        this.kyNangService = kyNangService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KyNangResponse>>> getAllKyNang() {
        ApiResponse<List<KyNangResponse>> response = kyNangService.getAllKyNang();
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<KyNangResponse>>> searchKyNang(@RequestParam("keyword") String keyword) {
        ApiResponse<List<KyNangResponse>> response = kyNangService.searchKyNang(keyword);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<KyNangResponse>> createKyNang(@Valid @RequestBody KyNangRequest request) {
        ApiResponse<KyNangResponse> response = kyNangService.createKyNang(request);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
}