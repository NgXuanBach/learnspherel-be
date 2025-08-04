package com.learnspherel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ReviewCourseResponseDTO {
    private Long maDanhGia;
    private Long maNguoiDung;
    private String tenNguoiDung;
    private Integer diemSo;
    private String binhLuan;
    private LocalDateTime ngayTao;
}
