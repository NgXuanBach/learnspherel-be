package com.learnspherel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MyCourseResponse {
    private Long maKhoaHoc;
    private String tieuDe;
    private String type; // "MIỄN PHÍ" hoặc "TRẢ PHÍ"
    private Double tienDo; // Phần trăm hoàn thành
    private Double rating; // Điểm đánh giá trung bình
    private String thoiLuong; // Thời lượng (ví dụ: "2.5 Giờ")
    private String status; // "in-progress" hoặc "completed"
    private String anhDaiDien;
}