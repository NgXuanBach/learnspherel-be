package com.learnspherel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminRevenueReportDto {
    private int month;           // Tháng
    private int year;            // Năm
    private Long maKhoaHoc;
    private String tenKhoaHoc;
    private Long soDonHang;      // ĐỔI: từ long -> Long
    private Double tongDoanhThu; // ĐỔI: từ double -> Double
    private Double adminNhanDuoc;// ĐỔI: từ double -> Double
}
