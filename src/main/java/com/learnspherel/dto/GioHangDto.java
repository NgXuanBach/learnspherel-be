package com.learnspherel.dto;

import com.learnspherel.entity.enums.TrangThaiThanhToan;
import com.learnspherel.entity.enums.TrinhDo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GioHangDto {
    private Long maGioHang;

    @NotNull(message = "{giohang.maNguoiDung.empty}")
    @Positive(message = "{giohang.maNguoiDung.invalid}")
    private Long maNguoiDung;
    @NotNull(message = "{giohang.maKhoaHoc.empty}")
    @Positive(message = "{giohang.maKhoaHoc.invalid}")
    private Long maKhoaHoc;
    private String anhDaiDienKhoaHoc;
    private String tenKhoaHoc;
    private TrinhDo trinhDo;
    private Double gia;
    private String trangThai;
    private TrangThaiThanhToan trangThaiThanhToan;
    private boolean coPhi;
    private LocalDateTime ngayThem;
}