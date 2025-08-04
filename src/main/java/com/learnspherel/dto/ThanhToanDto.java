package com.learnspherel.dto;

import com.learnspherel.entity.enums.PhuongThucThanhToan;
import com.learnspherel.entity.enums.TrangThaiGiaoDich;
import com.learnspherel.entity.enums.TrangThaiThanhToan;
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
public class ThanhToanDto {
    private Long maGiaoDich;
    @NotNull(message = "{thanh_toan.maDangKy.empty}")
    @Positive(message = "{thanh_toan.maDangKy.invalid}")
    private Long maDangKy;
    @NotNull(message = "{thanh_toan.soTien.empty}")
    @Positive(message = "{thanh_toan.soTien.invalid}")
    private Double soTien;
    private PhuongThucThanhToan phuongThuc;
    private String maGiaoDichNganHang;
    private TrangThaiGiaoDich trangThai;
    private LocalDateTime ngayGiaoDich;
    private KhoaHocDto khoaHoc; // Sử dụng KhoaHocDto thay vì KhoaHoc entity
    private TrangThaiThanhToan trangThaiThanhToan;
}