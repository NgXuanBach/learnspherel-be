package com.learnspherel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueByMonthResponse {
    private String monthYear;
    private List<CourseRevenue> courses;
    private Double totalRevenue;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseRevenue {
        private String tieuDe;
        private Integer soDonHang;
        private Double doanhThu;
        private String tyLe;
    }
}