package com.learnspherel.dto;

import jakarta.validation.constraints.NotNull;

public class DangKyKhoaHocRequest {
    @NotNull(message = "Mã khóa học không được để trống")
    private Long maKhoaHoc;

    public Long getMaKhoaHoc() {
        return maKhoaHoc;
    }

    public void setMaKhoaHoc(Long maKhoaHoc) {
        this.maKhoaHoc = maKhoaHoc;
    }
}
