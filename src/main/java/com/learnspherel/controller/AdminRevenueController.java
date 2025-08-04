package com.learnspherel.controller;

import com.learnspherel.dto.AdminRevenueReportDto;
import com.learnspherel.dto.ApiResponse;
import com.learnspherel.service.DangKyKhoaHocService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/revenue")
@RequiredArgsConstructor
public class AdminRevenueController {

    private final DangKyKhoaHocService adminRevenueService;

    // Lấy chi tiết báo cáo doanh thu theo tháng/năm (list)
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<List<AdminRevenueReportDto>>> getRevenueReport(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        ApiResponse<List<AdminRevenueReportDto>> res = adminRevenueService.getRevenueReport(month, year);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }

    // Lấy tổng doanh thu admin nhận
    @GetMapping("/total")
    public ResponseEntity<ApiResponse<Double>> getTotalRevenue(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        ApiResponse<Double> res = adminRevenueService.getTotalRevenue(month, year);
        return new ResponseEntity<>(res, HttpStatus.valueOf(res.getStatusCode()));
    }
}

